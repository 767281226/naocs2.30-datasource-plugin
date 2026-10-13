package com.alibaba.nacos.plugin.datasource.kingbase;

import com.alibaba.nacos.plugin.datasource.dialect.DatabaseDialect;
import com.alibaba.nacos.plugin.datasource.dialect.KingbaseDatabaseDialect;
import com.alibaba.nacos.plugin.datasource.manager.DatabaseDialectManager;
import com.alibaba.nacos.plugin.datasource.mapper.Mapper;
import com.kingbase8.Driver;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import static org.junit.Assert.*;

public class KingbasePluginTest {

    @Test
    public void discoversEveryKingbaseMapperThroughSpi() {
        Set<String> tables = new HashSet<>();
        int count = 0;
        for (Mapper mapper : ServiceLoader.load(Mapper.class)) {
            if ("kingbase".equals(mapper.getDataSource())) {
                count++;
                assertTrue("Duplicate mapper for " + mapper.getTableName(), tables.add(mapper.getTableName()));
                String key = Arrays.asList("his_config_info", "config_tags_relation").contains(mapper.getTableName())
                        ? "nid" : "id";
                assertArrayEquals(new String[] {key}, mapper.getPrimaryKeyGeneratedKeys());
            }
        }
        assertEquals(9, count);
        assertEquals(new HashSet<>(Arrays.asList("config_info", "config_info_aggr", "config_info_beta",
                "config_info_tag", "config_tags_relation", "his_config_info", "group_capacity",
                "tenant_capacity", "tenant_info")), tables);
    }

    @Test
    public void discoversDialectAndPreservesOffsetFirstBinding() {
        DatabaseDialect dialect = DatabaseDialectManager.getInstance().getDialect("kingbase");
        assertTrue(dialect instanceof KingbaseDatabaseDialect);
        assertEquals("SELECT 1 OFFSET ? LIMIT ?", dialect.getLimitPageSqlWithMark("SELECT 1").trim());
        assertEquals("SELECT 1 OFFSET 40 LIMIT 20", dialect.getLimitPageSql("SELECT 1", 3, 20));
        assertEquals("SELECT 1 OFFSET 7 LIMIT 20", dialect.getLimitPageSqlWithOffset("SELECT 1", 7, 20));
        assertEquals("SELECT 1 LIMIT ?", dialect.getLimitTopSqlWithMark("SELECT 1").trim());
        assertEquals(40, dialect.getPagePrevNum(3, 20));
        assertEquals(20, dialect.getPageLastNum(3, 20));
    }

    @Test
    public void loadsJava8KingbaseDriverAndAcceptsNativeUrl() throws Exception {
        assertTrue(new Driver().acceptsURL("jdbc:kingbase8://localhost:54321/nacos"));
    }
}
