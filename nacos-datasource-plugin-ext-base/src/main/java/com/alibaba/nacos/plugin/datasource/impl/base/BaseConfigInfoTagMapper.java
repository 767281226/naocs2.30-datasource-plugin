package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.ConfigInfoTagMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseConfigInfoTagMapper extends ConfigInfoTagMapperByMySql {
    @Override
    public MapperResult findAllConfigInfoTagForDumpAllFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.findAllConfigInfoTagForDumpAllFetchRows(context), context);
    }
}