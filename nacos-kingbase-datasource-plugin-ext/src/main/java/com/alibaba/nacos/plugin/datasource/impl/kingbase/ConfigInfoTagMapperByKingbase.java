package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigInfoTagMapper;

/** KingbaseES ConfigInfoTag mapper for PostgreSQL compatibility mode. */
public class ConfigInfoTagMapperByKingbase extends BaseConfigInfoTagMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
