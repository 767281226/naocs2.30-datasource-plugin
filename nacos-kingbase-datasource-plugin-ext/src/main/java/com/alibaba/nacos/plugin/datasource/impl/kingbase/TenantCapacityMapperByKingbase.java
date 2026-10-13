package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseTenantCapacityMapper;

/** KingbaseES TenantCapacity mapper for PostgreSQL compatibility mode. */
public class TenantCapacityMapperByKingbase extends BaseTenantCapacityMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
