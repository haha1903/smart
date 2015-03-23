package com.baidu.smart.ext

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher extends App {
  val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

  val server = new Server(port)
  val context = new WebAppContext()
  val resourceBase = getClass.getClassLoader.getResource("webapp").toExternalForm
  context setContextPath "/"
  context.setResourceBase(resourceBase)
  context.addEventListener(new ScalatraListener)

  server.setHandler(context)

  server.start
  server.join
}
