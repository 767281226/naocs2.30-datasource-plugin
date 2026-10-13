# Nacos 数据库适配插件

本项目基于 Nacos 2.3.0 的数据源扩展插件接口开发，编译目标为 Java 8。各数据库的适配版本、JDBC 驱动、初始化 SQL、连接配置和验证范围见对应模块的 README。

## 工程结构

| 模块与文档 | 职责 |
| --- | --- |
| [nacos-datasource-plugin-ext-base](nacos-datasource-plugin-ext-base/README.md) | 公共数据库方言、Mapper 适配和 SPI 管理。 |
| [nacos-postgresql-datasource-plugin-ext](nacos-postgresql-datasource-plugin-ext/README.md) | PostgreSQL 独立插件。 |
| [nacos-kingbase-datasource-plugin-ext](nacos-kingbase-datasource-plugin-ext/README.md) | 人大金仓 KingbaseES 独立插件。 |
| [nacos-all-datasource-plugin-ext](nacos-all-datasource-plugin-ext/README.md) | 打包所有数据库插件及其 JDBC 驱动的汇总包。 |

已编译的可部署 JAR 和 SHA-256 校验文件见 [dist 产物说明](dist/README.md)。

## 公共构建方式

在项目根目录使用 JDK 8 和 Maven 执行：

```shell
mvn clean package
```

该命令构建全部模块并运行已有测试。每个模块的最终 JAR 位于其 `target/` 目录，产物版本为 `1.0.0-SNAPSHOT`。部署时选择最终插件 JAR，`original-*.jar` 是 Maven Shade 打包前的产物。

单独构建某个插件时，使用 `-pl` 选择模块，使用 `-am` 构建其依赖；具体命令见对应模块 README。

通过 Nacos 源码依赖引入插件时，先安装到本地 Maven 仓库：

```shell
mvn clean install
```

再按所选插件 README 中的 Maven 坐标添加依赖。

## 公共加载流程

1. 选择目标数据库的独立插件包，或选择汇总插件包。
2. 将最终 JAR 放入 Nacos 安装目录的 `plugins/`。Nacos 2.3.0 的标准 `bin/startup.sh` 已通过 `loader.path` 加载该目录，自定义启动命令也应配置此路径。
3. 按目标数据库插件 README 导入初始化 SQL，配置 `conf/application.properties` 中的数据源标识、JDBC URL、账号和驱动。
4. 保存配置后按原部署方式重启 Nacos，并在目标数据库验证配置读写。

标准启动脚本的单机启动示例：

```shell
sh bin/startup.sh -m standalone
```

集群部署沿用原有启动方式。数据库权限、模式、初始化脚本行为和专属配置以对应插件文档为准。

## 扩展新插件

新建数据库插件 Maven 模块，在父工程 `pom.xml` 注册模块，并在汇总模块中添加依赖。实现对应数据库方言、Mapper 和 SPI 注册，提供 JDBC 驱动依赖、初始化 SQL、必要的验证以及模块 README。

公共扩展接口和 SPI 文件格式见[基础模块文档](nacos-datasource-plugin-ext-base/README.md#3扩展数据库方言和-mapper)。

## 其他版本

Nacos 2.1 的适配项目见 [nacos-multidatasource](https://github.com/wuchubuzai2018/nacos-multidatasource)。
