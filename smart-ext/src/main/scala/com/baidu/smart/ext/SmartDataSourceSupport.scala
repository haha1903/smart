package com.scalaone.smart.ext

import javax.sql.DataSource

import com.scalaone.smart.core.ConfigSupport

import scala.slick.driver.MySQLDriver.simple._

trait SmartDataSourceSupport extends ConfigSupport {

  val ds: DataSource

  implicit def dynSession = Database.dynamicSession

  def dynTxn[T](f: => T): T = Database.forDataSource(ds) withDynTransaction (f)

  def txn[T](f: Session => T): T = Database.forDataSource(ds) withTransaction (f)

  def withDynSession[T](f: => T): T = Database.forDataSource(ds) withDynSession (f)

  def withSession[T](f: Session => T): T = Database.forDataSource(ds) withSession (f)
}
