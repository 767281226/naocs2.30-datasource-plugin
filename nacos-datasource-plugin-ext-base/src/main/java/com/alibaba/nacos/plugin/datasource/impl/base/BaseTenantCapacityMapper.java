package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.TenantCapacityMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseTenantCapacityMapper extends TenantCapacityMapperByMySql {
    @Override
    public MapperResult getCapacityList4CorrectUsage(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.getCapacityList4CorrectUsage(context), context);
    }
}