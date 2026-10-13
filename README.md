# Nacos数据库适配插件

## 一、插件概述

### 1.1、简介

本项目基于 Nacos 2.3.0 的数据源扩展插件接口开发，支持 PostgreSQL 和人大金仓 KingbaseES V8R6（PG 兼容模式）。

如需Nacos2.1支持，请移步个人的如下这个仓库：

https://github.com/wuchubuzai2018/nacos-multidatasource

编译目标为 Java 8，Nacos 依赖版本为 2.3.0。

### 2.2、插件工程结构说明

nacos-datasource-plugin-ext-base工程为数据库插件操作的适配抽象。

nacos-all-datasource-plugin-ext工程可打包所有适配的数据库插件

nacos-postgresql-datasource-plugin-ext工程可打包适配Postgresql的数据库插件

nacos-kingbase-datasource-plugin-ext工程可打包适配人大金仓 KingbaseES 的数据库插件，内置 `cn.com.kingbase:kingbase8:8.6.0` JDBC 驱动。

## 二、下载和使用

### 2.1、插件引入

方式一：使用postgresql作为依赖引入到Nacos主分支源码中，例如：

```xml
<dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-postgresql-datasource-plugin-ext</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
```

方式二：下载当前插件项目源码，打包为jar包，将该文件的路径配置到startup.sh文件中，使用Nacos的loader.path机制指定该插件的路径，可修改startup.sh中的loader.path参数的位置进行指定。

### 2.2、修改数据库配置文件

在application.properties文件中声明postgresql的配置信息：

```java
spring.datasource.platform=postgresql
db.url.0=jdbc:postgresql://127.0.0.1:5432/nacos?tcpKeepAlive=true&reWriteBatchedInserts=true&ApplicationName=nacos_java
db.user=nacos
db.password=nacos
db.pool.config.driverClassName=org.postgresql.Driver
```

### 2.3、导入Postgresql的数据库脚本文件

导入nacos-postgresql的脚本文件，脚本文件在nacos-postgresql-datasource-plugin-ext/src/main/resources/schema文件夹下面.

上面操作完成后，启动Nacos即可。

## 三、其他数据库插件开发

可参考nacos-postgresql-datasource-plugin-ext工程，新创建Maven项目，实现AbstractDatabaseDialect类，重写相关的分页操作逻辑与方法，并创建相应的mapper实现。

## 四、人大金仓 KingbaseES 插件

### 4.1、适配环境

- Nacos：2.3.0。
- KingbaseES：`V008R006C008B0020 on x86_64-pc-linux-gnu`，64 位，PG 兼容模式。
- Java：8 或以上；默认 JDBC 驱动为 `cn.com.kingbase:kingbase8:8.6.0`。
- 数据源标识：`kingbase`；驱动类：`com.kingbase8.Driver`；URL 前缀：`jdbc:kingbase8://`。

本插件复用公共 Mapper 中的 PG 分页适配，注册 9 个金仓 Mapper 和独立数据库方言。历史清理按 `nid` 分批删除，配置 ID 不作为历史记录主键。初始化 SQL 包含 Nacos 2.3.0 所需的 12 张表、主键、唯一约束、索引、容量字段默认值和初始管理员。

金仓 Mapper 同时补齐 Nacos 2.3.0 原实现中的基础配置分页参数、灰度配置转储参数、模糊查询的 Group 占位符，以及标签计数的命名空间和标签参数。

### 4.2、打包和加载

在项目根目录执行：

```shell
# 构建独立金仓插件及其公共依赖，运行测试
mvn clean package -pl nacos-kingbase-datasource-plugin-ext -am

# 或构建全部模块（汇总插件包含 PostgreSQL 和金仓插件）
mvn clean package
```

独立插件产物：

```text
nacos-kingbase-datasource-plugin-ext/target/nacos-kingbase-datasource-plugin-ext-1.0.0-SNAPSHOT.jar
```

仓库的 [dist/](dist/) 目录同时提供已编译的金仓独立包、汇总包和 `SHA256SUMS` 校验文件，可直接下载使用；具体内容见 [产物说明](dist/README.md)。

将该 JAR 放到 Nacos 2.3.0 安装目录的 `plugins/` 下。标准 `bin/startup.sh` 已通过 `loader.path` 加载该目录；使用自定义启动命令时也需要把该目录加入 `loader.path`。JAR 已包含金仓 JDBC 驱动和公共适配代码，选择最终产物（不要选择 `original-*.jar`）。如果使用 `nacos-all-datasource-plugin-ext` 汇总包，加载汇总包即可。

通过 Nacos 源码依赖引入时，可添加：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-kingbase-datasource-plugin-ext</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 4.3、初始化数据库

使用 UTF-8 数据库。由运行 Nacos 的数据库用户，在与 JDBC `currentSchema` 相同的模式内执行：

```text
nacos-kingbase-datasource-plugin-ext/src/main/resources/schema/nacos-kingbase.sql
```

例如在 Linux 上用金仓客户端导入（`-W` 提示输入密码）：

```shell
ksql -h 127.0.0.1 -p 54321 -U nacos -d nacos -W -v ON_ERROR_STOP=1 -f nacos-kingbase.sql
```

脚本用于空数据库的一次性初始化，采用事务执行，不删除已有表；遇到同名表会报错。示例使用 `public` 模式。使用其他模式时，应先将导入会话的 `search_path` 设置为目标模式，并同步修改 JDBC 的 `currentSchema`。数据库用户需要对表具备增删改查权限，对自增序列具备使用权限。

执行 `SHOW database_mode;` 确认 PG 模式，并检查 `SHOW ora_input_emptystr_isnull;` 的值为 `off`。Nacos 使用空字符串表示默认命名空间，须保留空字符串与 NULL 的区别；如果该参数已被改为 `on`，应由数据库管理员恢复此行为后再导入和启动。

脚本沿用 Nacos 2.3.0 的初始管理员 `nacos`，初始密码为 `nacos`，首次登录后修改。

### 4.4、配置 Nacos

将以下配置复制到 Nacos 2.3.0 安装目录下的 `conf/application.properties`，替换该文件原有的数据库连接配置。适用于 KingbaseES V008R006C008B0020 的 PG 兼容模式。

示例连接本机 `127.0.0.1:54321`，数据库名为 `nacos`，数据库用户为 `nacos`，模式为 `public`。部署时替换为实际环境的地址、端口、库名、模式、用户名和密码。

```properties
# 数据源标识，必须与金仓插件注册的 kingbase 一致
spring.sql.init.platform=kingbase
# 兼容旧配置名称；已有此项时也设置为 kingbase
spring.datasource.platform=kingbase

# 数据源数量；一个数据库连接使用 1，配置编号从 0 开始
db.num=1

# 金仓 JDBC 地址；currentSchema 对应初始化 SQL 所在模式
db.url.0=jdbc:kingbase8://127.0.0.1:54321/nacos?currentSchema=public&tcpKeepAlive=true&ApplicationName=nacos_java

# 数据库账号，密码替换为实际值
db.user.0=nacos
db.password.0=CHANGE_ME

# 金仓 JDBC 驱动与连接检测 SQL
db.pool.config.driverClassName=com.kingbase8.Driver
db.pool.config.connectionTestQuery=SELECT 1
```

需要显式配置连接池时，可在同一文件追加以下示例，连接数按实际负载调整，超时单位为毫秒：

```properties
db.pool.config.maximumPoolSize=20
db.pool.config.minimumIdle=2
db.pool.config.connectionTimeout=30000
db.pool.config.validationTimeout=5000
```

`spring.sql.init.platform` 是 Nacos 2.3.0 优先读取的配置；`spring.datasource.platform` 是旧名称，两个配置应保持一致。`db.user.0`、`db.password.0` 与 `db.url.0` 对应同一个数据源。

启用配置时依次完成：

1. 将金仓独立插件 JAR 或汇总插件 JAR 放入 Nacos 安装目录的 `plugins/`。
2. 使用上述数据库账号，在 `currentSchema` 指定的模式下导入 `nacos-kingbase.sql`，确认 PG 模式保留空字符串。
3. 保存 `conf/application.properties` 后重启 Nacos；使用标准启动脚本单机启动时执行 `sh bin/startup.sh -m standalone`，集群部署按原有启动方式启动。

配置读取及连接池参数可核对 [Nacos 2.3.0 数据源源码](https://github.com/alibaba/nacos/blob/2.3.0/persistence/src/main/java/com/alibaba/nacos/persistence/datasource/ExternalDataSourceProperties.java)和[连接池源码](https://github.com/alibaba/nacos/blob/2.3.0/persistence/src/main/java/com/alibaba/nacos/persistence/datasource/DataSourcePoolProperties.java)。

### 4.5、验证范围

模块测试覆盖 SPI 加载、驱动 URL、分页参数、默认命名空间 CRUD、自增主键、配置唯一约束、容量表默认值，以及共享配置 ID 的历史记录分批清理。SQL 执行测试使用 H2 的 PostgreSQL 模式；这不等同于在目标 KingbaseES 实例上完成联调。

上线前在目标实例检查：默认命名空间和自定义命名空间的配置发布、读取、更新、删除；配置分页和历史记录；灰度配置、标签和容量管理。测试时保留 Nacos 日志，以便核对数据库异常。

### 4.6、参考资料

- [KingbaseES V8 JDBC 连接说明](https://help.kingbase.com.cn/v8/development/client-interfaces/jdbc/jdbc-2.html)。
- [金仓 JDBC 8.6.0 的 Maven 坐标](https://central.sonatype.com/artifact/cn.com.kingbase/kingbase8/8.6.0)。
- [Nacos 2.3.0 官方表结构](https://github.com/alibaba/nacos/blob/2.3.0/distribution/conf/mysql-schema.sql)。
- [Nacos 2.3.0 启动脚本](https://github.com/alibaba/nacos/blob/2.3.0/distribution/bin/startup.sh)。
