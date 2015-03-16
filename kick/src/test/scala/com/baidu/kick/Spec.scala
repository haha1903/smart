package com.baidu.kick

object Listener extends App with PushSupport {
  listen(PushApp("p1", "m1")) { m =>
    println(s"ah: $m")
  }
  push(PushApp("p1", "m1"), "test", Map("s" -> "a"))
}

object Pusher extends App with PushSupport {
  push(PushApp("p1", "m1"), "test", Map("s" -> "a"))
}
