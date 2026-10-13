package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseGroupCapacityMapper;

/** KingbaseES GroupCapacity mapper for PostgreSQL compatibility mode. */
public class GroupCapacityMapperByKingbase extends BaseGroupCapacityMapper {

    @Override
    public String getDataSource() {
        return DatabaseTypeConstant.KINGBASE;
    }
}
