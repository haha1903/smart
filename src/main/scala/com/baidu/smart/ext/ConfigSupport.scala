package com.baidu.smart.ext

import com.typesafe.config.ConfigFactory

/**
 * Created by changhai on 14/12/4.
 */
trait ConfigSupport {
  val cfg = ConfigFactory.load()

  def getString(path: String) = cfg.getString(path)

  def getLong(path: String) = cfg.getLong(path)

  def getDouble(path: String) = cfg.getDouble(path)

  def getInt(path: String) = cfg.getInt(path)

  def getBoolean(path: String) = cfg.getBoolean(path)
}
