package com.alibaba.nacos.plugin.datasource.impl.postgresql;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.PostgresqlMapperResultAdapter;
import com.alibaba.nacos.plugin.datasource.impl.mysql.HistoryConfigInfoMapperByMySql;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/** PostgreSQL history mapper compatible with the Nacos 2.3.x mapper API. */
public class HistoryConfigInfoMapperByPostgresql extends HistoryConfigInfoMapperByMySql {
    @Override
    public MapperResult removeConfigHistory(MapperContext context) {
        MapperResult result = super.removeConfigHistory(context);
        result.setSql("WITH temp_table AS (SELECT id FROM his_config_info WHERE gmt_modified < ? LIMIT ?) "
                + "DELETE FROM his_config_info WHERE id IN (SELECT id FROM temp_table)");
        return result;
    }

    @Override
    public MapperResult pageFindConfigHistoryFetchRows(MapperContext context) {
        return PostgresqlMapperResultAdapter.adapt(super.pageFindConfigHistoryFetchRows(context), context);
    }

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.POSTGRESQL;
    }
}