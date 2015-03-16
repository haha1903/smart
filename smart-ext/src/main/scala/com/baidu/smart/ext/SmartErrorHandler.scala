package com.baidu.smart.ext

import grizzled.slf4j.Logging
import org.scalatra.{ErrorHandler, ScalatraServlet}

/**
 * Smart Powerlink
 *
 * @since 14-7-9
 * @author changhai
 */
trait SmartErrorHandler extends ScalatraServlet with Logging {
  private[this] val handler: ErrorHandler = {
    case e => {
      logger.info(e.getMessage, e)
      Failure(e.getMessage)
    }
  }

  error(handler)
}
