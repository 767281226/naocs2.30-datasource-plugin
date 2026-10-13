package com.alibaba.nacos.plugin.datasource.impl.kingbase;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.impl.base.BaseConfigTagsRelationMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** KingbaseES ConfigTagsRelation mapper for PostgreSQL compatibility mode. */
public class ConfigTagsRelationMapperByKingbase extends BaseConfigTagsRelationMapper {

    @Override
    public MapperResult findConfigInfo4PageCountRows(MapperContext context) {
        MapperResult result = super.findConfigInfo4PageCountRows(context);
        // Nacos 2.3.0's count query omits the tenant and tag bindings.
        List<Object> parameters = new ArrayList<>();
        parameters.add(context.getWhereParameter(FieldConstant.TENANT_ID));
        parameters.addAll(result.getParamList());
        Collections.addAll(parameters, (String[]) context.getWhereParameter(FieldConstant.TAG_ARR));
        return new MapperResult(result.getSql(), parameters);
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
