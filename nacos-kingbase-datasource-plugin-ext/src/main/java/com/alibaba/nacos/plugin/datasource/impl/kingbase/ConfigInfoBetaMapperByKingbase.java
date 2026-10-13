package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigInfoBetaMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Arrays;

/** KingbaseES ConfigInfoBeta mapper for PostgreSQL compatibility mode. */
public class ConfigInfoBetaMapperByKingbase extends BaseConfigInfoBetaMapper {

    @Override
    public MapperResult findAllConfigInfoBetaForDumpAllFetchRows(MapperContext context) {
        // Nacos 2.3.0 supplies pagination parameters even though its MySQL SQL uses literals.
        String sql = "SELECT t.id,data_id,group_id,tenant_id,app_name,content,md5,gmt_modified,beta_ips,encrypted_data_key "
                + "FROM (SELECT id FROM config_info_beta ORDER BY id LIMIT ? OFFSET ?) g, "
                + "config_info_beta t WHERE g.id = t.id";
        return new MapperResult(sql, Arrays.<Object>asList(context.getPageSize(), context.getStartRow()));
    }

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
