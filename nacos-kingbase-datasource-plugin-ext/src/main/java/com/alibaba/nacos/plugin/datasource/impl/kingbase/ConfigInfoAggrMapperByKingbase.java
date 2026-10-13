package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigInfoAggrMapper;

/** KingbaseES ConfigInfoAggr mapper for PostgreSQL compatibility mode. */
public class ConfigInfoAggrMapperByKingbase extends BaseConfigInfoAggrMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
