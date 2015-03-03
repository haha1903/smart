package com.baidu.smart.ext

import java.sql
import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.text.SimpleDateFormat
import java.util.Date

import com.baidu.smart.ext.MySQLDriverExt._

import scala.language.implicitConversions

/**
 * Created by changhai on 15/1/14.
 */
object SlickDataTypes {
  implicit def utilDateColumnType = new UtilDateJdbcType

  implicit def adStatusColumnType = new AdStatusJdbcType

  implicit def timestampColumnTypeExt = new TimestampJdbcTypeExt
}

object AdStatus extends Enumeration {
  val NO_LAUNCH, END, PAUSED, LAUNCHING = Value
}

class AdStatusJdbcType extends DriverJdbcType[AdStatus.Value] {

  import com.baidu.smart.ext.AdStatus.Value

  def sqlType = java.sql.Types.VARCHAR

  def setValue(v: Value, p: PreparedStatement, idx: Int) = p.setString(idx, v.toString())

  override def getValue(r: ResultSet, idx: Int) = AdStatus.withName(r.getString(idx))

  def updateValue(v: Value, r: ResultSet, idx: Int) = r.updateString(idx, v.toString)

  override def valueToSQLLiteral(value: Value): String = "'" + value.toString + "'"
}

class UtilDateJdbcType extends DriverJdbcType[Date] {
  private val format = new SimpleDateFormat("yyyy-MM-dd")

  implicit def utilDate2sqlDate(v: Date) = new sql.Date(v.getTime)

  def sqlType = java.sql.Types.DATE

  def setValue(v: Date, p: PreparedStatement, idx: Int) = p.setDate(idx, v)

  def getValue(r: ResultSet, idx: Int) = r.getDate(idx)

  def updateValue(v: Date, r: ResultSet, idx: Int) = r.updateDate(idx, v)

  override def valueToSQLLiteral(value: Date) = "'" + format.format(value) + "'"
}

class TimestampJdbcTypeExt extends DriverJdbcType[Timestamp] {
  private val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def sqlType = java.sql.Types.TIMESTAMP

  def setValue(v: Timestamp, p: PreparedStatement, idx: Int) = p.setTimestamp(idx, v)

  def getValue(r: ResultSet, idx: Int) = r.getTimestamp(idx)

  def updateValue(v: Timestamp, r: ResultSet, idx: Int) = r.updateTimestamp(idx, v)

  override def valueToSQLLiteral(value: Timestamp) = "'" + format.format(value) + "'"
}