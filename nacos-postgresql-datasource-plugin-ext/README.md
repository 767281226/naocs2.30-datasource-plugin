# PostgreSQL 数据源插件

[返回项目首页](../README.md)

## 1、适配信息

- Nacos：2.3.0，编译目标为 Java 8。
- 数据源标识：`postgresql`。
- JDBC 驱动：`org.postgresql:postgresql:42.7.8`。
- 驱动类：`org.postgresql.Driver`。
- JDBC URL 前缀：`jdbc:postgresql://`。

本模块注册 9 个 PostgreSQL Mapper 和 `PostgresqlDatabaseDialect`，复用公共基础模块的分页适配。独立插件 JAR 包含 PostgreSQL JDBC 驱动和公共适配代码。

## 2、打包和引入

在项目根目录执行：

```shell
mvn clean package -pl nacos-postgresql-datasource-plugin-ext -am
```

产物路径：

```text
nacos-postgresql-datasource-plugin-ext/target/nacos-postgresql-datasource-plugin-ext-1.0.0-SNAPSHOT.jar
```

将最终 JAR 放入 Nacos 安装目录的 `plugins/`。公共加载步骤见[项目首页](../README.md#公共加载流程)。使用[汇总插件](../nacos-all-datasource-plugin-ext/README.md)时，加载汇总包即可。

通过 Nacos 源码依赖引入时，先在项目根目录执行 `mvn clean install`，再添加：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-postgresql-datasource-plugin-ext</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 3、初始化数据库

使用 UTF-8 数据库，由 Nacos 的数据库用户导入[初始化 SQL](src/main/resources/schema/nacos-pg.sql)。示例数据库名、用户名均为 `nacos`，模式为 `public`。

在项目根目录执行以下命令，按提示输入数据库密码：

```shell
psql -h 127.0.0.1 -p 5432 -U nacos -d nacos -W -v ON_ERROR_STOP=1 -f nacos-postgresql-datasource-plugin-ext/src/main/resources/schema/nacos-pg.sql
```

该脚本包含 `DROP TABLE IF EXISTS`，会重建同名表；用于新库初始化，已有数据需先备份并安排迁移。使用其他模式时，应设置导入会话的 `search_path`，并同步调整 JDBC 的 `currentSchema`。

运行 Nacos 的数据库用户需要具备表的增删改查权限和自增序列的使用权限。脚本创建初始管理员 `nacos`，初始密码为 `nacos`，首次登录后修改。

## 4、配置 Nacos

将以下配置写入 Nacos 的 `conf/application.properties`，替换原有数据库配置：

```properties
spring.sql.init.platform=postgresql
spring.datasource.platform=postgresql

db.num=1
db.url.0=jdbc:postgresql://127.0.0.1:5432/nacos?currentSchema=public&tcpKeepAlive=true&reWriteBatchedInserts=true&ApplicationName=nacos_java
db.user.0=nacos
db.password.0=CHANGE_ME

db.pool.config.driverClassName=org.postgresql.Driver
db.pool.config.connectionTestQuery=SELECT 1
```

将地址、端口、库名、模式、用户名和 `CHANGE_ME` 替换为实际值。`currentSchema` 必须与初始化 SQL 所在模式一致，两个平台配置均使用 `postgresql`。

需要显式配置连接池时，可以在同一文件追加以下示例。连接数按实际负载调整，超时单位为毫秒：

```properties
db.pool.config.maximumPoolSize=20
db.pool.config.minimumIdle=2
db.pool.config.connectionTimeout=30000
db.pool.config.validationTimeout=5000
```

放置插件 JAR、导入 SQL、保存配置后，按原部署方式重启 Nacos。标准启动脚本单机启动命令为 `sh bin/startup.sh -m standalone`。

## 5、验证范围

当前模块未提供 PostgreSQL 数据库集成测试。部署到目标实例后，应检查配置发布、读取、更新、删除、分页、历史记录、命名空间、灰度配置、标签和容量管理。

公共构建命令见[项目首页](../README.md#公共构建方式)，Mapper 和方言扩展方式见[基础模块文档](../nacos-datasource-plugin-ext-base/README.md)。
