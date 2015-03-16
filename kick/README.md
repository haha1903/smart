# Kick #

## Build & Run ##

    执行 **sbt publish** 发布到 maven 库

# 使用

sbt 中

    libraryDependencies += **"com.baidu" %% "kick" % "1.0.0"**

maven 中

    <dependency>
        <groupId>com.baidu</groupId>
        <artifactId>kick_2.11</artifactId>
        <version>1.0.0</version/
    </dependency>

# 配置

在 application.conf 中配置如下，如果不修改，则无需配置任何内容，默认 enable = true

    kick {
      enable = true
      host = cq01-rdqa-pool106.cq01.baidu.com
      port = 8379
      database = 0
    }

# 代码示例

## listener

    object Listener extends App with PushSupport {
      listen(PushApp("p1", "m1")) { m =>
        println(s"ah: $m")
      }
      push(PushApp("p1", "m1"), "test", Map("s" -> "a")) // local push
    }

## pusher

    object Pusher extends App with PushSupport {
      push(PushApp("p1", "m1"), "test", Map("s" -> "a")) // remote push
    }

## 本地 push

    如果 listener 与 pusher 在一个 jvm 中，会使用本地方法调用 push，效果相同，不走网络，同步调用，易于调试。
