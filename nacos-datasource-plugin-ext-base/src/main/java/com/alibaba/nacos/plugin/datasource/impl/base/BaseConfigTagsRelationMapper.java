package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.ConfigTagsRelationMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseConfigTagsRelationMapper extends ConfigTagsRelationMapperByMySql {
    @Override
    public MapperResult findConfigInfo4PageFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.findConfigInfo4PageFetchRows(context), context);
    }

    @Override
    public MapperResult findConfigInfoLike4PageFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.findConfigInfoLike4PageFetchRows(context), context);
    }
}