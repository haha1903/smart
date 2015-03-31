package com.scalaone.kick

import java.util.concurrent.{Callable, FutureTask, Executors}

import scala.concurrent.Future

object Listener extends App with PushSupport {
  listen(PushApp("p1", "m1")) { m =>
    println(s"ah: $m")
    Thread.sleep(2000)
    println(s"ok: $m")
  }
  push(PushApp("p1", "m1"), "test", Map("s" -> "a"))
}

object Pusher extends App with PushSupport {
  0 to 10 foreach { i =>
    push(PushApp("p1", "m1"), s"test$i", Map("s" -> "a"))
  }
}

object T extends App {
  val executor = Executors.newSingleThreadExecutor()
  val f = new FutureTask[String](new Callable[String] {
    def call(): String = "haha"
  })
  executor.execute(f)
  println(f.get())
}