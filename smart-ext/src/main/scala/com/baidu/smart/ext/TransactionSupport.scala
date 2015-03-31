package com.scalaone.smart.ext

import org.scalatra.{MatchedRoute, ScalatraBase}

/**
 * Smart Powerlink
 *
 * @since 14-7-3
 * @author changhai
 */
trait TransactionSupport extends ScalatraBase {
  val datasource: SmartDataSourceSupport

  override protected def invoke(matchedRoute: MatchedRoute): Option[Any] = datasource.dynTxn {
    super.invoke(matchedRoute)
  }
}
