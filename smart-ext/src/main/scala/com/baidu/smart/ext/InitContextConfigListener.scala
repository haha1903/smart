package com.scalaone.smart.ext

import javax.servlet.{ServletContextEvent, ServletContextListener}

/**
 * Created by changhai on 15/3/19.
 */
class InitContextConfigListener extends ServletContextListener {
  SpringContextConfig.init()

  def contextInitialized(sce: ServletContextEvent) {
  }

  def contextDestroyed(sce: ServletContextEvent) {}
}
