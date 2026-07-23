package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.ConfigInfoAggrMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseConfigInfoAggrMapper extends ConfigInfoAggrMapperByMySql {
    @Override
    public MapperResult findConfigInfoAggrByPageFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.findConfigInfoAggrByPageFetchRows(context), context);
    }
}