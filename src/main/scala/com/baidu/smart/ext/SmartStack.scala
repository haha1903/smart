package com.baidu.smart.ext

import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport

trait SmartStack extends ScalatraServlet with ServiceSupport with SmartValueResult with JacksonJsonSupport with Logging with ErrorHandler {
}