package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseTenantInfoMapper;

/** KingbaseES TenantInfo mapper for PostgreSQL compatibility mode. */
public class TenantInfoMapperByKingbase extends BaseTenantInfoMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
