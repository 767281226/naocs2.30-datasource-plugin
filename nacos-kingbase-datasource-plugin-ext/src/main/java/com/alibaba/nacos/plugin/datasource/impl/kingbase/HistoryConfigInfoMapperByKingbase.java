package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.PostgresqlMapperResultAdapter;
import com.alibaba.nacos.plugin.datasource.impl.mysql.HistoryConfigInfoMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** KingbaseES history mapper compatible with the Nacos 2.3.0 API. */
public class HistoryConfigInfoMapperByKingbase extends HistoryConfigInfoMapperByMySql {

    @Override
    public MapperResult removeConfigHistory(MapperContext context) {
        MapperResult result = super.removeConfigHistory(context);
        // Multiple history rows can share a config ID; limit deletion by the unique history NID.
        result.setSql("DELETE FROM his_config_info WHERE nid IN "
                + "(SELECT nid FROM his_config_info WHERE gmt_modified < ? ORDER BY nid LIMIT ?)");
        return result;
    }

    @Override
    public MapperResult pageFindConfigHistoryFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.pageFindConfigHistoryFetchRows(context), context);
    }

    @Override
    public String[] getPrimaryKeyGeneratedKeys() {
        return new String[] {"nid"};
    }

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
