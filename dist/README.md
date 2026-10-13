# 可部署的插件 JAR

本目录保存 2026-10-13 使用 Java 8、`mvn clean package` 编译的产物，13 项测试全部通过。

| 文件 | 用途 |
| --- | --- |
| [nacos-kingbase-datasource-plugin-ext-1.0.0-SNAPSHOT.jar](nacos-kingbase-datasource-plugin-ext-1.0.0-SNAPSHOT.jar) | 人大金仓独立插件，包含 Kingbase JDBC 8.6.0 驱动和公共适配代码。 |
| [nacos-all-datasource-plugin-ext-1.0.0-SNAPSHOT.jar](nacos-all-datasource-plugin-ext-1.0.0-SNAPSHOT.jar) | 汇总插件，包含 PostgreSQL 和人大金仓插件及 JDBC 驱动。 |
| [SHA256SUMS](SHA256SUMS) | 上述两个 JAR 的 SHA-256 校验值。 |

金仓适配目标为 Nacos 2.3.0、KingbaseES V008R006C008B0020（PG 兼容模式）。选择独立包或汇总包之一放入 Nacos 的 `plugins/` 目录，并按[部署说明](../README.md#四人大金仓-kingbasees-插件)导入 SQL 和修改数据库配置。

SQL 执行测试使用 H2 的 PostgreSQL 模式，目标 KingbaseES 实例仍需联调。

后续更新源码时，应执行 `mvn clean package`，复制对应模块 `target/` 内最终 JAR 到本目录，并同步更新 `SHA256SUMS`。
