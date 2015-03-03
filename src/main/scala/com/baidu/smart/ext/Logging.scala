package com.baidu.smart.ext

import grizzled.slf4j.Logger

/**
 * Smart Powerlink
 *
 * @since 14-7-9
 * @author changhai
 */
trait Logging {
  @transient lazy val logger: Logger = Logger(getClass)
}
