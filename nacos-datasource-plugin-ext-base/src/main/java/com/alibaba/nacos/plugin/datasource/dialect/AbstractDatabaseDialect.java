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

import com.alibaba.nacos.plugin.datasource.constants.PrimaryKeyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DatabaseDialect.
 * Default limit for mysql,postgresql
 * @author Long Yu
 */
public abstract class AbstractDatabaseDialect implements DatabaseDialect {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDatabaseDialect.class);

    @Override
    public int getPagePrevNum(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    @Override
    public int getPageLastNum(int page, int pageSize) {
        return pageSize;
    }

    @Override
    public String getLimitTopSqlWithMark(String sql) {
        LOGGER.info("getLimitTopSqlWithMark:runing limit top sql: {}", sql);
        return sql + " LIMIT ? ";
    }

    @Override
    public String getLimitPageSqlWithMark(String sql) {
        LOGGER.info("getLimitPageSqlWithMark:runing limit top sql: {}", sql);
        return sql + " LIMIT ?,? ";
    }

    @Override
    public String getLimitPageSql(String sql, int pageNo, int pageSize) {
        LOGGER.info("getLimitPageSql:runing  limit top sql: {}", sql);
        return sql + "  LIMIT " + getPagePrevNum(pageNo, pageSize) + " , " + pageSize;
    }

    @Override
    public String getLimitPageSqlWithOffset(String sql, int startOffset, int pageSize){
        LOGGER.info("getLimitPageSqlWithOffset:runing limit top sql: {}", sql);
        return sql + "  LIMIT " + startOffset + " , " + pageSize;
    }

    @Override
    public String[] getReturnPrimaryKeys() {
        return PrimaryKeyConstant.LOWER_RETURN_PRIMARY_KEYS;
    }

}
