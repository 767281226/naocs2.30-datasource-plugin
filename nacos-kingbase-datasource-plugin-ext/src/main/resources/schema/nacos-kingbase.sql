/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

-- Nacos 2.3.0 / KingbaseES V008R006C008B0020, PostgreSQL compatibility mode.
-- Run once in an empty database, using the same schema and user as the JDBC connection.
-- Keep empty strings distinct from NULL (ora_input_emptystr_isnull must be off).
-- No DROP statements: existing tables cause an error and the transaction rolls back.
BEGIN;

CREATE TABLE "config_info" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NULL,
    "content" text NOT NULL,
    "md5" varchar(32) NULL,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "src_user" text NULL,
    "src_ip" varchar(50) NULL,
    "app_name" varchar(128) NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "c_desc" varchar(256) NULL,
    "c_use" varchar(64) NULL,
    "effect" varchar(64) NULL,
    "type" varchar(64) NULL,
    "c_schema" text NULL,
    "encrypted_data_key" text NOT NULL,
    CONSTRAINT "uk_configinfo_datagrouptenant" UNIQUE ("data_id", "group_id", "tenant_id")
);
COMMENT ON TABLE "config_info" IS '配置信息';
COMMENT ON COLUMN "config_info"."tenant_id" IS '租户字段，空字符串表示默认命名空间';
COMMENT ON COLUMN "config_info"."encrypted_data_key" IS '密钥';

CREATE TABLE "config_info_aggr" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NOT NULL,
    "datum_id" varchar(255) NOT NULL,
    "content" text NOT NULL,
    "gmt_modified" timestamp(6) NOT NULL,
    "app_name" varchar(128) NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    CONSTRAINT "uk_configinfoaggr_datagrouptenantdatum" UNIQUE ("data_id", "group_id", "tenant_id", "datum_id")
);
COMMENT ON TABLE "config_info_aggr" IS '聚合配置';

CREATE TABLE "config_info_beta" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NOT NULL,
    "app_name" varchar(128) NULL,
    "content" text NOT NULL,
    "beta_ips" varchar(1024) NULL,
    "md5" varchar(32) NULL,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "src_user" text NULL,
    "src_ip" varchar(50) NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "encrypted_data_key" text NOT NULL,
    CONSTRAINT "uk_configinfobeta_datagrouptenant" UNIQUE ("data_id", "group_id", "tenant_id")
);
COMMENT ON TABLE "config_info_beta" IS '灰度配置';

CREATE TABLE "config_info_tag" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NOT NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "tag_id" varchar(128) NOT NULL,
    "app_name" varchar(128) NULL,
    "content" text NOT NULL,
    "md5" varchar(32) NULL,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "src_user" text NULL,
    "src_ip" varchar(50) NULL,
    CONSTRAINT "uk_configinfotag_datagrouptenanttag" UNIQUE ("data_id", "group_id", "tenant_id", "tag_id")
);
COMMENT ON TABLE "config_info_tag" IS '带标签的配置';

CREATE TABLE "config_tags_relation" (
    "id" bigint NOT NULL,
    "tag_name" varchar(128) NOT NULL,
    "tag_type" varchar(64) NULL,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NOT NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "nid" bigserial NOT NULL PRIMARY KEY,
    CONSTRAINT "uk_configtagrelation_configidtag" UNIQUE ("id", "tag_name", "tag_type")
);
CREATE INDEX "idx_config_tags_tenant_id" ON "config_tags_relation" ("tenant_id");
COMMENT ON TABLE "config_tags_relation" IS '配置与标签的关系';
COMMENT ON COLUMN "config_tags_relation"."id" IS '配置ID，由应用提供';
COMMENT ON COLUMN "config_tags_relation"."nid" IS '自增主键';

CREATE TABLE "group_capacity" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "group_id" varchar(128) NOT NULL DEFAULT '',
    "quota" integer NOT NULL DEFAULT 0,
    "usage" integer NOT NULL DEFAULT 0,
    "max_size" integer NOT NULL DEFAULT 0,
    "max_aggr_count" integer NOT NULL DEFAULT 0,
    "max_aggr_size" integer NOT NULL DEFAULT 0,
    "max_history_count" integer NOT NULL DEFAULT 0,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "uk_group_id" UNIQUE ("group_id")
);
COMMENT ON TABLE "group_capacity" IS '集群、各Group容量信息';

CREATE TABLE "his_config_info" (
    "id" bigint NOT NULL,
    "nid" bigserial NOT NULL PRIMARY KEY,
    "data_id" varchar(255) NOT NULL,
    "group_id" varchar(128) NOT NULL,
    "app_name" varchar(128) NULL,
    "content" text NOT NULL,
    "md5" varchar(32) NULL,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "src_user" text NULL,
    "src_ip" varchar(50) NULL,
    "op_type" char(10) NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "encrypted_data_key" text NOT NULL
);
CREATE INDEX "idx_gmt_create" ON "his_config_info" ("gmt_create");
CREATE INDEX "idx_gmt_modified" ON "his_config_info" ("gmt_modified");
CREATE INDEX "idx_did" ON "his_config_info" ("data_id");
COMMENT ON TABLE "his_config_info" IS '配置变更历史';
COMMENT ON COLUMN "his_config_info"."id" IS '配置ID，多条历史记录可共享同一个配置ID';
COMMENT ON COLUMN "his_config_info"."nid" IS '历史记录自增主键';

CREATE TABLE "tenant_capacity" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "tenant_id" varchar(128) NOT NULL DEFAULT '',
    "quota" integer NOT NULL DEFAULT 0,
    "usage" integer NOT NULL DEFAULT 0,
    "max_size" integer NOT NULL DEFAULT 0,
    "max_aggr_count" integer NOT NULL DEFAULT 0,
    "max_aggr_size" integer NOT NULL DEFAULT 0,
    "max_history_count" integer NOT NULL DEFAULT 0,
    "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "uk_tenant_id" UNIQUE ("tenant_id")
);
COMMENT ON TABLE "tenant_capacity" IS '租户容量信息';

CREATE TABLE "tenant_info" (
    "id" bigserial NOT NULL PRIMARY KEY,
    "kp" varchar(128) NOT NULL,
    "tenant_id" varchar(128) NULL DEFAULT '',
    "tenant_name" varchar(128) NULL DEFAULT '',
    "tenant_desc" varchar(256) NULL,
    "create_source" varchar(32) NULL,
    "gmt_create" bigint NOT NULL,
    "gmt_modified" bigint NOT NULL,
    CONSTRAINT "uk_tenant_info_kptenantid" UNIQUE ("kp", "tenant_id")
);
CREATE INDEX "idx_tenant_info_tenant_id" ON "tenant_info" ("tenant_id");
COMMENT ON TABLE "tenant_info" IS '命名空间信息';

CREATE TABLE "users" (
    "username" varchar(50) NOT NULL PRIMARY KEY,
    "password" varchar(500) NOT NULL,
    "enabled" boolean NOT NULL
);
COMMENT ON TABLE "users" IS '用户';

CREATE TABLE "roles" (
    "username" varchar(50) NOT NULL,
    "role" varchar(50) NOT NULL,
    CONSTRAINT "uk_username_role" UNIQUE ("username", "role")
);
COMMENT ON TABLE "roles" IS '用户角色';

CREATE TABLE "permissions" (
    "role" varchar(50) NOT NULL,
    "resource" varchar(255) NOT NULL,
    "action" varchar(8) NOT NULL,
    CONSTRAINT "uk_role_permission" UNIQUE ("role", "resource", "action")
);
COMMENT ON TABLE "permissions" IS '角色权限';

-- Nacos 2.3.0's initial administrator (password: nacos).
INSERT INTO "users" ("username", "password", "enabled")
VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
INSERT INTO "roles" ("username", "role") VALUES ('nacos', 'ROLE_ADMIN');

COMMIT;
