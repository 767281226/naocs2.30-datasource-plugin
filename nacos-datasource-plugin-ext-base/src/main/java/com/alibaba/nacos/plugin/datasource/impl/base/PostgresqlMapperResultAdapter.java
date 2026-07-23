package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Collections;
import java.util.List;

/** Adapts Nacos MySQL pagination SQL to PostgreSQL pagination syntax. */
public final class PostgresqlMapperResultAdapter {
    private static final String MARK_LIMIT = "LIMIT ?,?";

    private PostgresqlMapperResultAdapter() {
    }

    public static MapperResult adapt(MapperResult result, MapperContext context) {
        String mysqlLimit = "LIMIT " + context.getStartRow() + "," + context.getPageSize();
        String postgresqlLimit = "LIMIT " + context.getPageSize() + " OFFSET " + context.getStartRow();
        String sql = result.getSql().replace(mysqlLimit, postgresqlLimit);
        if (sql.contains(MARK_LIMIT)) {
            sql = sql.replace(MARK_LIMIT, "LIMIT ? OFFSET ?");
            swapLastTwo(result.getParamList());
        }
        result.setSql(sql);
        return result;
    }

    private static void swapLastTwo(List<Object> parameters) {
        if (parameters != null && parameters.size() >= 2) {
            int last = parameters.size() - 1;
            Collections.swap(parameters, last - 1, last);
        }
    }
}