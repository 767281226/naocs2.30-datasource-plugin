package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.ConfigInfoBetaMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseConfigInfoBetaMapper extends ConfigInfoBetaMapperByMySql {
    @Override
    public MapperResult findAllConfigInfoBetaForDumpAllFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.findAllConfigInfoBetaForDumpAllFetchRows(context), context);
    }
}