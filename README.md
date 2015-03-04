# SmartExt #

## Build & Run ##

    执行 **sbt publish** 发布到 maven 库

# 使用

sbt 中

    libraryDependencies += "com.baidu" %% "smartext" % "1.0.0"

maven 中

    <dependency>
        <groupId>com.baidu</groupId>
        <artifactId>smartext_2.11</artifactId>
        <version>1.0.0</version/
    </dependency>

# 功能

- 提供 SmartErrorHandler，兼容前端 json 数据格式
- 提供 SmartValueResult，兼容前端 json 数据格式
- 提供 ServiceSupport，支持 Swagger
- 提供 ConfigSupport，支持 Hocon config
- 提供 Sharding & MySQLDriverExt 支持 DB Sharding
- 提供 SmartDataSourceSupport，支持 Slick
