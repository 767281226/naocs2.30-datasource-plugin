package com.alibaba.nacos.plugin.datasource.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.impl.kingbase.ConfigInfoMapperByKingbase;
import com.alibaba.nacos.plugin.datasource.impl.kingbase.ConfigInfoBetaMapperByKingbase;
import com.alibaba.nacos.plugin.datasource.impl.kingbase.ConfigTagsRelationMapperByKingbase;
import com.alibaba.nacos.plugin.datasource.impl.kingbase.HistoryConfigInfoMapperByKingbase;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;
import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

/** Executes schema and mapper SQL in H2 PG mode; deployment still needs a KingbaseES smoke test. */
public class KingbaseSchemaTest {
    private Connection connection;

    @Before
    public void initializeEmptyDatabase() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/schema/nacos-kingbase.sql"), StandardCharsets.UTF_8)) {
            RunScript.execute(connection, reader);
        }
    }

    @After
    public void closeDatabase() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void defaultNamespaceCrudAndPaginationReturnGeneratedIds() throws Exception {
        ConfigInfoMapperByKingbase mapper = new ConfigInfoMapperByKingbase();
        String sql = mapper.insert(Arrays.asList("data_id", "group_id", "content", "encrypted_data_key"));
        try (PreparedStatement statement = connection.prepareStatement(sql, mapper.getPrimaryKeyGeneratedKeys())) {
            for (int i = 0; i < 3; i++) {
                statement.setString(1, "config-" + i);
                statement.setString(2, "DEFAULT_GROUP");
                statement.setString(3, "人大金仓配置-" + i);
                statement.setString(4, "");
                assertEquals(1, statement.executeUpdate());
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    assertTrue(keys.next());
                    assertTrue(keys.getLong(1) > 0);
                }
            }
        }
        MapperContext context = new MapperContext(1, 1);
        context.putWhereParameter(FieldConstant.TENANT_ID, "");
        try (PreparedStatement statement = prepare(mapper.findAllConfigInfoFetchRows(context));
                ResultSet rows = statement.executeQuery()) {
            assertTrue(rows.next());
            assertEquals("config-1", rows.getString("data_id"));
            assertEquals("人大金仓配置-1", rows.getString("content"));
            assertEquals("", rows.getString("tenant_id"));
            assertFalse(rows.next());
        }
        try (PreparedStatement statement = prepare(mapper.findAllConfigInfoBaseFetchRows(context));
                ResultSet rows = statement.executeQuery()) {
            assertTrue(rows.next());
            assertEquals("config-1", rows.getString("data_id"));
            assertFalse(rows.next());
        }
        try (PreparedStatement statement = connection.prepareStatement(
                mapper.update(Arrays.asList("content"), Arrays.asList("data_id", "tenant_id")))) {
            statement.setString(1, "更新");
            statement.setString(2, "config-1");
            statement.setString(3, "");
            assertEquals(1, statement.executeUpdate());
        }
        try (PreparedStatement statement = connection.prepareStatement(mapper.delete(Arrays.asList("data_id", "tenant_id")))) {
            statement.setString(1, "config-1");
            statement.setString(2, "");
            assertEquals(1, statement.executeUpdate());
        }
    }

    @Test
    public void minimalCapacityInsertsUseNacosDefaults() throws Exception {
        try (Statement statement = connection.createStatement()) {
            for (String table : Arrays.asList("group_capacity", "tenant_capacity")) {
                String column = "group_capacity".equals(table) ? "group_id" : "tenant_id";
                statement.executeUpdate("INSERT INTO " + table + " (" + column + ") VALUES ('')");
                try (ResultSet rows = statement.executeQuery("SELECT * FROM " + table)) {
                    assertTrue(rows.next());
                    for (String quota : Arrays.asList("quota", "usage", "max_size", "max_aggr_count",
                            "max_aggr_size", "max_history_count")) {
                        assertEquals(0, rows.getInt(quota));
                    }
                    assertNotNull(rows.getTimestamp("gmt_create"));
                    assertNotNull(rows.getTimestamp("gmt_modified"));
                }
            }
        }
    }

    @Test
    public void historyCleanupLimitsRowsWithSharedConfigIdAndPreservesRecentHistory() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO his_config_info "
                    + "(id,data_id,group_id,content,encrypted_data_key,gmt_modified) VALUES "
                    + "(42,'example','DEFAULT_GROUP','old-1','','2025-01-01'),"
                    + "(42,'example','DEFAULT_GROUP','old-2','','2025-01-02'),"
                    + "(42,'example','DEFAULT_GROUP','recent','','2026-02-01')");
        }
        MapperContext context = new MapperContext();
        context.putWhereParameter(FieldConstant.START_TIME, Timestamp.valueOf("2026-01-01 00:00:00"));
        context.putWhereParameter(FieldConstant.LIMIT_SIZE, 1);
        MapperResult result = new HistoryConfigInfoMapperByKingbase().removeConfigHistory(context);
        try (PreparedStatement statement = prepare(result)) {
            assertEquals(1, statement.executeUpdate());
            assertEquals(1, statement.executeUpdate());
            assertEquals(0, statement.executeUpdate());
        }
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery("SELECT content FROM his_config_info")) {
            assertTrue(rows.next());
            assertEquals("recent", rows.getString(1));
            assertFalse(rows.next());
        }
    }

    @Test
    public void tagRelationGeneratesNidAndKeepsSuppliedConfigId() throws Exception {
        ConfigTagsRelationMapperByKingbase mapper = new ConfigTagsRelationMapperByKingbase();
        String sql = mapper.insert(Arrays.asList("id", "tag_name", "data_id", "group_id"));
        try (PreparedStatement statement = connection.prepareStatement(sql, mapper.getPrimaryKeyGeneratedKeys())) {
            statement.setLong(1, 42L);
            statement.setString(2, "test");
            statement.setString(3, "example");
            statement.setString(4, "DEFAULT_GROUP");
            assertEquals(1, statement.executeUpdate());
            try (ResultSet keys = statement.getGeneratedKeys()) {
                assertTrue(keys.next());
                assertEquals(1L, keys.getLong(1));
            }
        }
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery("SELECT id,nid FROM config_tags_relation")) {
            assertTrue(rows.next());
            assertEquals(42L, rows.getLong("id"));
            assertEquals(1L, rows.getLong("nid"));
        }
    }

    @Test
    public void defaultNamespaceConfigUniquenessIsEnforced() throws Exception {
        String sql = "INSERT INTO config_info (data_id,group_id,content,encrypted_data_key) "
                + "VALUES ('example','DEFAULT_GROUP','content','')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            try {
                statement.executeUpdate(sql);
                fail("Duplicate configuration must violate its unique constraint");
            } catch (SQLException expected) {
                assertEquals("23505", expected.getSQLState());
            }
        }
    }

    @Test
    public void betaDumpBindsPaginationParameters() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO config_info_beta (data_id,group_id,content,encrypted_data_key) "
                    + "VALUES ('beta-1','DEFAULT_GROUP','first',''),('beta-2','DEFAULT_GROUP','second','')");
        }
        MapperResult result = new ConfigInfoBetaMapperByKingbase()
                .findAllConfigInfoBetaForDumpAllFetchRows(new MapperContext(1, 1));
        try (PreparedStatement statement = prepare(result); ResultSet rows = statement.executeQuery()) {
            assertTrue(rows.next());
            assertEquals("beta-2", rows.getString("data_id"));
            assertFalse(rows.next());
        }
    }

    @Test
    public void groupFilterAndTagCountBindEveryParameter() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO config_info (data_id,group_id,content,encrypted_data_key) "
                    + "VALUES ('example','DEFAULT_GROUP','content','')");
            statement.executeUpdate("INSERT INTO config_tags_relation (id,tag_name,data_id,group_id) "
                    + "SELECT id,'test',data_id,group_id FROM config_info");
        }
        MapperContext context = new MapperContext(0, 10);
        context.putWhereParameter(FieldConstant.TENANT_ID, "");
        context.putWhereParameter(FieldConstant.DATA_ID, "example");
        context.putWhereParameter(FieldConstant.GROUP_ID, "DEFAULT_GROUP");
        context.putWhereParameter(FieldConstant.CONTENT, "content");
        context.putWhereParameter(FieldConstant.TAG_ARR, new String[] {"test", "another"});
        MapperResult filtered = new ConfigInfoMapperByKingbase().findConfigInfoBaseLikeFetchRows(context);
        try (PreparedStatement statement = prepare(filtered); ResultSet rows = statement.executeQuery()) {
            assertTrue(rows.next());
            assertEquals("example", rows.getString("data_id"));
            assertFalse(rows.next());
        }
        MapperResult counted = new ConfigTagsRelationMapperByKingbase().findConfigInfo4PageCountRows(context);
        try (PreparedStatement statement = prepare(counted); ResultSet rows = statement.executeQuery()) {
            assertTrue(rows.next());
            assertEquals(1, rows.getInt(1));
        }
    }

    private PreparedStatement prepare(MapperResult result) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(result.getSql());
        for (int i = 0; i < result.getParamList().size(); i++) {
            statement.setObject(i + 1, result.getParamList().get(i));
        }
        return statement;
    }
}
