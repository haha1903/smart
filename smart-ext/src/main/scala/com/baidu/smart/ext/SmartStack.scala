package com.scalaone.smart.ext

import grizzled.slf4j.Logging
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport

trait SmartStack extends ScalatraServlet with ServiceSupport with SmartValueResult with JacksonJsonSupport with Logging with SmartErrorHandler {
}