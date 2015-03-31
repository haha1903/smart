package com.scalaone.smart.ext

import javax.servlet._

import org.slf4j.MDC

/**
 * Smart Powerlink
 *
 * @since 14-6-30
 * @author changhai
 */
trait MDCFilterSupport extends Filter {
  def isDefined: Boolean

  def userId: String

  def ucName: String

  def userName: String

  @Override
  def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    try {
      if (isDefined) {
        MDC.put("userId", userId)
        MDC.put("ucName", ucName)
        MDC.put("userName", userName)
        MDC.put("userInfo", s" User[userId = $userId, userName = $userName, ucName = $ucName]")
      }
      chain.doFilter(request, response)
    } finally {
      MDC.clear()
    }
  }

  def destroy() {}
}
