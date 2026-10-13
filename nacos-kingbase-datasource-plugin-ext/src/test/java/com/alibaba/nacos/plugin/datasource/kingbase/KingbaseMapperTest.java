package com.alibaba.nacos.plugin.datasource.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.impl.kingbase.ConfigInfoMapperByKingbase;
import com.alibaba.nacos.plugin.datasource.mapper.Mapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class KingbaseMapperTest {
    private static final Pattern MYSQL_LIMIT = Pattern.compile("(?i)LIMIT\\s+(?:\\d+|\\?)\\s*,");

    @Test
    public void allMapperQueriesUseCompatiblePaginationAndCompleteBindings() throws Exception {
        MapperContext context = new MapperContext(40, 20);
        context.putWhereParameter(FieldConstant.TENANT_ID, "");
        context.putWhereParameter(FieldConstant.TENANT, "");
        context.putWhereParameter(FieldConstant.DATA_ID, "example");
        context.putWhereParameter(FieldConstant.GROUP_ID, "DEFAULT_GROUP");
        context.putWhereParameter(FieldConstant.APP_NAME, "test-app");
        context.putWhereParameter(FieldConstant.CONTENT, "sample");
        context.putWhereParameter(FieldConstant.ID, 10L);
        context.putWhereParameter(FieldConstant.LAST_MAX_ID, 10L);
        context.putWhereParameter(FieldConstant.START_TIME, Timestamp.valueOf("2026-01-01 00:00:00"));
        context.putWhereParameter(FieldConstant.END_TIME, Timestamp.valueOf("2026-02-01 00:00:00"));
        context.putWhereParameter(FieldConstant.LIMIT_SIZE, 20);
        context.putWhereParameter(FieldConstant.DATUM_ID, Arrays.asList("first", "second"));
        context.putWhereParameter(FieldConstant.IS_IN, true);
        context.putWhereParameter(FieldConstant.TAG_ARR, new String[] {"first", "second"});
        context.putWhereParameter(FieldConstant.IDS, Arrays.asList(1L, 2L));
        List<String> errors = new ArrayList<>();
        int tested = 0;
        for (Mapper mapper : ServiceLoader.load(Mapper.class)) {
            if (!"kingbase".equals(mapper.getDataSource())) {
                continue;
            }
            // Inspect each inherited database-specific query from the Nacos 2.3.0 API.
            for (Method method : mapper.getClass().getMethods()) {
                if (method.getReturnType() == MapperResult.class
                        && Arrays.equals(method.getParameterTypes(), new Class<?>[] {MapperContext.class})) {
                    MapperResult result = (MapperResult) method.invoke(mapper, context);
                    if (MYSQL_LIMIT.matcher(result.getSql()).find()) {
                        errors.add(method.getName() + ": " + result.getSql());
                    }
                    long placeholders = result.getSql().chars().filter(c -> c == '?').count();
                    if (placeholders != result.getParamList().size()) {
                        errors.add(method.getName() + ": " + placeholders + " placeholders but "
                                + result.getParamList().size() + " parameters");
                    }
                    tested++;
                }
            }
        }
        assertTrue("Expected to verify the complete mapper API", tested > 50);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void placeholderPaginationBindsPageSizeBeforeOffset() {
        MapperContext context = new MapperContext(40, 20);
        context.putWhereParameter(FieldConstant.TENANT_ID, "");
        ConfigInfoMapperByKingbase mapper = new ConfigInfoMapperByKingbase();
        MapperResult all = mapper.findAllConfigInfoFetchRows(context);
        assertTrue(all.getSql().contains("LIMIT ? OFFSET ?"));
        assertEquals(Arrays.<Object>asList("", 20, 40), all.getParamList());
        MapperResult base = mapper.findAllConfigInfoBaseFetchRows(context);
        assertTrue(base.getSql().contains("LIMIT ? OFFSET ?"));
        assertEquals(Arrays.<Object>asList(20, 40), base.getParamList());
    }

    @Test
    public void changeCursorUsesZeroOffsetEvenWhenContextHasAnotherStartRow() {
        MapperContext context = new MapperContext(40, 20);
        context.putWhereParameter(FieldConstant.LAST_MAX_ID, 123L);
        MapperResult result = new ConfigInfoMapperByKingbase().findChangeConfigFetchRows(context);
        assertTrue(result.getSql(), result.getSql().contains("id > 123"));
        assertTrue(result.getSql(), result.getSql().contains("LIMIT 20 OFFSET 0"));
    }
}
