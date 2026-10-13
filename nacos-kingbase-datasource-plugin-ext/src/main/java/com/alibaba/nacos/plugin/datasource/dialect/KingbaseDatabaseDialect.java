package com.alibaba.nacos.plugin.datasource.dialect;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;

/** KingbaseES V8R6 dialect for PostgreSQL compatibility mode. */
public class KingbaseDatabaseDialect extends AbstractDatabaseDialect {

    @Override
    public String getType() {
        return DatabaseTypeConstant.KINGBASE;
    }

    @Override
    public String getLimitPageSqlWithMark(String sql) {
        // The caller binds the offset before the page size.
        return sql + " OFFSET ? LIMIT ? ";
    }

    @Override
    public String getLimitPageSql(String sql, int pageNo, int pageSize) {
        return getLimitPageSqlWithOffset(sql, getPagePrevNum(pageNo, pageSize), pageSize);
    }

    @Override
    public String getLimitPageSqlWithOffset(String sql, int startOffset, int pageSize) {
        return sql + " OFFSET " + startOffset + " LIMIT " + pageSize;
    }
}
