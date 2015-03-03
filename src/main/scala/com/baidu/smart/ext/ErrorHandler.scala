package com.baidu.smart.ext

import org.scalatra.ScalatraServlet

/**
 * Smart Powerlink
 *
 * @since 14-7-9
 * @author changhai
 */
trait ErrorHandler extends ScalatraServlet with Logging {
  error {
    case e => {
      logger.info(e.getMessage, e)
      Failure(e.getMessage)
    }
  }
}
