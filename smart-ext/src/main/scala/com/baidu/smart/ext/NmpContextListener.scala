package com.baidu.smart.ext

import com.baidu.nmp.base.utils.concurrent.GlobalExecutor
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.{ApplicationEvent, ApplicationListener}

/**
 * Created by changhai on 15/3/9.
 */
class NmpContextListener[T <: ApplicationEvent] extends ApplicationListener[T] {
  override def onApplicationEvent(event: T) {
    event match {
      case e: ContextClosedEvent => GlobalExecutor.INSTANCE.shutdown()
      case _ =>
    }
  }
}
