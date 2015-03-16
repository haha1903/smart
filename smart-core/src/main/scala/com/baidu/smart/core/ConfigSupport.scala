package com.baidu.smart.core

import com.typesafe.config.ConfigFactory

/**
 * Created by changhai on 14/12/4.
 */
object ConfigSupport {
  // support switch profile
  val cfg = {
    val c = ConfigFactory.load()
    if (c.hasPath("profile")) c.getConfig(c.getString("profile")) else c
  }
}

trait ConfigSupport {
  // export cfg
  val cfg = ConfigSupport.cfg

  def getOr[T](path: String, f: String => T, default: => T) = try f(path) catch {
    case e: Throwable => default
  }

  def getString(path: String) = cfg.getString(path)

  def getStringOr(path: String, default: => String) = getOr(path, getString, default)

  def getLong(path: String) = cfg.getLong(path)

  def getLongOr(path: String, default: => Long) = getOr(path, getLong, default)

  def getDouble(path: String) = cfg.getDouble(path)

  def getDoubleOr(path: String, default: => Double) = getOr(path, getDouble, default)

  def getInt(path: String) = cfg.getInt(path)

  def getIntOr(path: String, default: => Int) = getOr(path, getInt, default)

  def getBoolean(path: String) = cfg.getBoolean(path)

  def getBooleanOr(path: String, default: => Boolean) = getOr(path, getBoolean, default)

  def getConfig(path: String) = cfg.getConfig(path)
}
