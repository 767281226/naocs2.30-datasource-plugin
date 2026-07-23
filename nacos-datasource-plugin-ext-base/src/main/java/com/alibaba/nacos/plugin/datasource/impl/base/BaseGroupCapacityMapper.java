package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.GroupCapacityMapperByMysql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseGroupCapacityMapper extends GroupCapacityMapperByMysql {
    @Override
    public MapperResult selectGroupInfoBySize(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.selectGroupInfoBySize(context), context);
    }
}