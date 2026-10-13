# 数据源插件公共基础模块

[返回项目首页](../README.md)

## 1、模块职责

本模块供数据库插件复用，包含数据库方言接口、默认实现、SPI 管理和公共 Mapper 适配。它作为其他插件的依赖打包，部署时使用数据库独立插件或汇总插件 JAR。

| 组成 | 作用 |
| --- | --- |
| `DatabaseTypeConstant`、`PrimaryKeyConstant` | 数据源标识与主键名称常量。 |
| `DatabaseDialect` | 分页 SQL、分页参数及主键返回规则接口。 |
| `AbstractDatabaseDialect`、`DefaultDatabaseDialect` | 默认分页和主键行为，供数据库方言继承与覆盖。 |
| `DatabaseDialectManager` | 通过 Nacos SPI 加载方言，并按数据源标识查找。 |
| `Base*Mapper` | 复用 Nacos 2.3.0 Mapper API，为具体数据库插件提供公共实现。 |
| `PostgresqlMapperResultAdapter` | 将 MySQL 风格分页转换为 PG 风格，并调整占位符分页的参数顺序。 |

## 2、构建和依赖

在项目根目录执行：

```shell
mvn clean package -pl nacos-datasource-plugin-ext-base -am
```

本模块以 `provided` 方式依赖 Nacos 2.3.0 的 `nacos-datasource-plugin` 和 `nacos-common`，由 Nacos 运行环境提供对应类。

数据库插件可以引入：

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-datasource-plugin-ext-base</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 3、扩展数据库方言和 Mapper

1. 在 `DatabaseTypeConstant` 中声明数据源标识。
2. 实现 `DatabaseDialect` 或继承 `AbstractDatabaseDialect`，覆盖目标数据库的分页 SQL 和主键规则，`getType()` 返回该数据源标识。
3. 实现目标数据库的 Mapper，按需要复用 `Base*Mapper`，`getDataSource()` 返回同一个标识。
4. 在数据库插件的 `src/main/resources/META-INF/services/` 下注册实现类。

SPI 文件名：

```text
com.alibaba.nacos.plugin.datasource.dialect.DatabaseDialect
com.alibaba.nacos.plugin.datasource.mapper.Mapper
```

注册文件按每行一个实现类的全限定类名填写。父工程的 Maven Shade 配置会合并这些服务文件。

复用公共 Mapper 时，应核对目标数据库的 SQL 语法、占位符数量、分页参数顺序、自增主键和空字符串行为。数据库专属的覆盖方法、驱动、初始化 SQL 和配置说明放在对应插件模块中。

实现示例见 [PostgreSQL 插件](../nacos-postgresql-datasource-plugin-ext/README.md)和[金仓插件](../nacos-kingbase-datasource-plugin-ext/README.md)。
