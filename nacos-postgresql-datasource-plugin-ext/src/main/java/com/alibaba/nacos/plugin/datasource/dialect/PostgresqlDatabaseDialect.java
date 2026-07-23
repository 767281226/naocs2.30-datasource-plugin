/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.plugin.datasource.dialect;

import com.alibaba.nacos.plugin.datasource.constants.DatabaseTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PostgreSQL database dialect.
 *
 * @author Long Yu
 */
public class PostgresqlDatabaseDialect extends AbstractDatabaseDialect {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlDatabaseDialect.class);

    @Override
    public String getType() {
        return DatabaseTypeConstant.POSTGRESQL;
    }

    @Override
    public String getLimitTopSqlWithMark(String sql) {
        LOGGER.info("PostgresqlDatabaseDialect getLimitTopSqlWithMark");
        return sql + " LIMIT ? ";
    }

    @Override
    public String getLimitPageSqlWithMark(String sql) {
        LOGGER.info("PostgresqlDatabaseDialect getLimitPageSqlWithMark");
        return sql + "  OFFSET ? LIMIT ? ";
    }

    @Override
    public String getLimitPageSql(String sql, int pageNo, int pageSize) {
        LOGGER.info("PostgresqlDatabaseDialect getLimitPageSql");
        return sql + "  OFFSET " + getPagePrevNum(pageNo, pageSize) + " LIMIT " + pageSize;
    }

    @Override
    public String getLimitPageSqlWithOffset(String sql, int startOffset, int pageSize) {
        LOGGER.info("PostgresqlDatabaseDialect getLimitPageSqlWithOffset");
        return sql + "  OFFSET " + startOffset + " LIMIT " + pageSize;
    }

}
