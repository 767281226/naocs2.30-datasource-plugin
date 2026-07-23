package com.alibaba.nacos.plugin.datasource.impl.base;

import com.alibaba.nacos.plugin.datasource.impl.mysql.ConfigInfoMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL-compatible implementation for Nacos 2.3.x. */
public class BaseConfigInfoMapper extends ConfigInfoMapperByMySql {
    private MapperResult adapt(MapperResult result, MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(result, context);
    }

    @Override public MapperResult findConfigInfoByAppFetchRows(MapperContext c) { return adapt(super.findConfigInfoByAppFetchRows(c), c); }
    @Override public MapperResult getTenantIdList(MapperContext c) { return adapt(super.getTenantIdList(c), c); }
    @Override public MapperResult getGroupIdList(MapperContext c) { return adapt(super.getGroupIdList(c), c); }
    @Override public MapperResult findAllConfigKey(MapperContext c) { return adapt(super.findAllConfigKey(c), c); }
    @Override public MapperResult findAllConfigInfoBaseFetchRows(MapperContext c) { return adapt(super.findAllConfigInfoBaseFetchRows(c), c); }
    @Override public MapperResult findAllConfigInfoFragment(MapperContext c) { return adapt(super.findAllConfigInfoFragment(c), c); }
    @Override public MapperResult findChangeConfigFetchRows(MapperContext c) { return adapt(super.findChangeConfigFetchRows(c), c); }
    @Override public MapperResult listGroupKeyMd5ByPageFetchRows(MapperContext c) { return adapt(super.listGroupKeyMd5ByPageFetchRows(c), c); }
    @Override public MapperResult findConfigInfoBaseLikeFetchRows(MapperContext c) { return adapt(super.findConfigInfoBaseLikeFetchRows(c), c); }
    @Override public MapperResult findConfigInfo4PageFetchRows(MapperContext c) { return adapt(super.findConfigInfo4PageFetchRows(c), c); }
    @Override public MapperResult findConfigInfoBaseByGroupFetchRows(MapperContext c) { return adapt(super.findConfigInfoBaseByGroupFetchRows(c), c); }
    @Override public MapperResult findConfigInfoLike4PageFetchRows(MapperContext c) { return adapt(super.findConfigInfoLike4PageFetchRows(c), c); }
    @Override public MapperResult findAllConfigInfoFetchRows(MapperContext c) { return adapt(super.findAllConfigInfoFetchRows(c), c); }
}