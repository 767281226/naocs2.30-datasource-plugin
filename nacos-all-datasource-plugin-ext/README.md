# 数据源插件汇总包

[返回项目首页](../README.md)

## 1、包含的插件

本模块将所有已适配的数据库插件、公共适配代码和 JDBC 驱动打包到同一个 JAR 中。

| 插件 | 数据源标识 | 数据库配置和初始化说明 |
| --- | --- | --- |
| PostgreSQL | `postgresql` | [PostgreSQL README](../nacos-postgresql-datasource-plugin-ext/README.md) |
| 人大金仓 KingbaseES | `kingbase` | [KingbaseES README](../nacos-kingbase-datasource-plugin-ext/README.md) |

每个 Nacos 实例通过数据源配置选择要使用的插件。

## 2、构建产物

在项目根目录执行：

```shell
mvn clean package -pl nacos-all-datasource-plugin-ext -am
```

产物路径：

```text
nacos-all-datasource-plugin-ext/target/nacos-all-datasource-plugin-ext-1.0.0-SNAPSHOT.jar
```

仓库也提供[已编译汇总包](../dist/nacos-all-datasource-plugin-ext-1.0.0-SNAPSHOT.jar)，构建记录和校验文件见[产物说明](../dist/README.md)。

## 3、部署方式

1. 将最终汇总 JAR 放入 Nacos 安装目录的 `plugins/`，加载步骤见[项目首页](../README.md#公共加载流程)。
2. 按目标数据库插件的 README 导入对应 SQL，配置 `conf/application.properties`。
3. 按原部署方式重启 Nacos，并验证配置读写。

选择汇总包或对应数据库独立包之一部署。汇总包已包含所需 JDBC 驱动。

通过 Nacos 源码依赖引入时，先在项目根目录执行 `mvn clean install`，再添加：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-all-datasource-plugin-ext</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 4、新增数据库插件

创建新的数据库插件模块后，在本模块的 `pom.xml` 中添加其依赖。父工程通过 Maven Shade 的 `ServicesResourceTransformer` 合并 SPI 注册文件，使汇总 JAR 保留各数据库的 Mapper、数据库方言和 JDBC 驱动注册。

数据库的兼容范围和测试范围以对应插件 README 为准。
