package com.baidu.smart.core

import com.typesafe.config.{ConfigException, ConfigFactory}

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

  def hasPath(path: String) = cfg.hasPath(path)

  def getOr[T](path: String, f: String => T, default: => T) = try f(path) catch {
    case e: Throwable => default
  }

  def get[T](path: String, f: String => T) = try f(path) catch {
    case e: ConfigException.Null => null.asInstanceOf[T]
  }

  def getString(path: String) = get(path, cfg.getString)

  def getStringOr(path: String, default: => String) = getOr(path, getString, default)

  def getLong(path: String) = get(path, cfg.getLong)

  def getLongOr(path: String, default: => Long) = getOr(path, getLong, default)

  def getDouble(path: String) = get(path, cfg.getDouble)

  def getDoubleOr(path: String, default: => Double) = getOr(path, getDouble, default)

  def getInt(path: String) = get(path, cfg.getInt)

  def getIntOr(path: String, default: => Int) = getOr(path, getInt, default)

  def getBoolean(path: String) = get(path, cfg.getBoolean)

  def getBooleanOr(path: String, default: => Boolean) = getOr(path, getBoolean, default)

  def getConfig(path: String) = get(path, cfg.getConfig)
}
