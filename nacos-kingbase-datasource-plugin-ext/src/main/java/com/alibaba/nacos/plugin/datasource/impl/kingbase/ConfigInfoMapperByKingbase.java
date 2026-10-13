package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.impl.base.PostgresqlMapperResultAdapter;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Arrays;

/** KingbaseES configuration mapper compatible with the Nacos 2.3.0 API. */
public class ConfigInfoMapperByKingbase extends BaseConfigInfoMapper {

    @Override
    public MapperResult findAllConfigInfoBaseFetchRows(MapperContext context) {
        // Nacos 2.3.0's MySQL implementation omits the pagination parameters here.
        String sql = "SELECT t.id,data_id,group_id,content,md5 FROM "
                + "(SELECT id FROM config_info ORDER BY id LIMIT ? OFFSET ?) g, config_info t WHERE g.id = t.id";
        return new MapperResult(sql, Arrays.<Object>asList(context.getPageSize(), context.getStartRow()));
    }

    @Override
    public MapperResult findChangeConfigFetchRows(MapperContext context) {
        // This query uses an ID cursor and always starts at offset zero.
        return PostgresqlMapperResultAdapter.adapt(super.findChangeConfigFetchRows(context),
                new MapperContext(0, context.getPageSize()));
    }

    @Override
    public MapperResult findConfigInfoBaseLikeFetchRows(MapperContext context) {
        MapperResult result = super.findConfigInfoBaseLikeFetchRows(context);
        // Nacos 2.3.0's optional group filter is missing its placeholder.
        result.setSql(result.getSql().replace(" AND group_id LIKE ", " AND group_id LIKE ? "));
        return result;
    }

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
