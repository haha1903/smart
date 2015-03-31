package com.scalaone.smart.core

import com.typesafe.config.{Config, ConfigException, ConfigFactory}

/**
 * Created by changhai on 14/12/4.
 */
object ConfigSupport {
  val rootCfg = ConfigFactory.load()
  // support switch profile
  val cfg = if (rootCfg.hasPath("profile")) rootCfg.getConfig(rootCfg.getString("profile")) else rootCfg
}

trait ConfigSupport {
  // export cfg
  val rootCfg = ConfigSupport.rootCfg
  val cfg = ConfigSupport.cfg

  def hasPath(path: String) = cfg.hasPath(path) || rootCfg.hasPath(path)

  def getOr[T](path: String, f: String => T, default: => T) = try f(path) catch {
    case e: Throwable => default
  }

  def get[T](path: String, f: (Config, String) => T) = {
    try f(cfg, path) catch {
      case e: ConfigException.Null => null.asInstanceOf[T]
      case e: Throwable => try f(rootCfg, path) catch {
        case e: ConfigException.Null => null.asInstanceOf[T]
      }
    }
  }

  def getString(path: String) = get(path, (c, p) => c.getString(p))

  def getStringOr(path: String, default: => String) = getOr(path, getString, default)

  def getLong(path: String) = get(path, (c, p) => c.getLong(p))

  def getLongOr(path: String, default: => Long) = getOr(path, getLong, default)

  def getDouble(path: String) = get(path, (c, p) => c.getDouble(p))

  def getDoubleOr(path: String, default: => Double) = getOr(path, getDouble, default)

  def getInt(path: String) = get(path, (c, p) => c.getInt(p))

  def getIntOr(path: String, default: => Int) = getOr(path, getInt, default)

  def getBoolean(path: String) = get(path, (c, p) => c.getBoolean(p))

  def getBooleanOr(path: String, default: => Boolean) = getOr(path, getBoolean, default)

  def getConfig(path: String) = get(path, (c, p) => c.getConfig(p))
}