package com.scalaone.kick

import com.redis._
import com.scalaone.smart.core.ConfigSupport
import grizzled.slf4j.Logging
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction}

case class PushApp(name: String, module: String, version: String = "1.0")

case class PushMessage(app: PushApp, message: String, data: Map[String, String])

case class PushAddress(host: String, port: Int, database: Int = 0)

object PushSupport {
  val listeners = scala.collection.mutable.Map[PushApp, PushMessage => Any]()
}

import com.scalaone.kick.PushSupport._

trait PushSupport extends Logging with ConfigSupport {
  val pushEnabled = getBooleanOr("kick.enable", true)
  lazy val pushAddress = PushAddress(getString("kick.host"), getInt("kick.port"), getInt("kick.database"))
  private[this] val MSG_KEY = "com.scalaone.kick.PushSupport.message"
  private[this] implicit val jsonFormats = DefaultFormats

  private[this] def channel(app: PushApp) = {
    val PushApp(name, module, version) = app
    s"$name|$module|$version"
  }

  private[this] def run[T](f: => T) = {
    if (!pushEnabled) {
      logger.warn("push has been disabled")
    } else {
      f
    }
  }

  def listen[T](app: PushApp)(f: PushMessage => T) = run {
    val r = new RedisClient(pushAddress.host, pushAddress.port, pushAddress.database)
    logger.info(s"registered local listener, app: $app")
    listeners += app -> f
    r.subscribe(channel(app))(mes => mes match {
      case m: M => val data = parse(m.message).extract[Map[String, String]]
        f(PushMessage(app, data(MSG_KEY), data - MSG_KEY))
      case s: S => logger.info(s"registered remote listener, app: $app")
      case u: U => logger.info(s"unregister listener, app: $app")
        listeners -= app
      case o => logger.info(s"other message: $o")
    })
  }

  def push(app: PushApp, message: String, data: Map[String, String] = Map()) = run {
    val r = new RedisClient(pushAddress.host, pushAddress.port, pushAddress.database)
    listeners.get(app) match {
      case Some(f) => logger.info(s"local push app: $app, message: $message, data: $data")
        f(PushMessage(app, message, data))
      case None => val json = toJson(data + (MSG_KEY -> message))
        logger.info(s"push app: $app, data: [$json]")
        r.publish(channel(app), json)
        r.disconnect
    }
  }

  private[this] def toJson(d: Any) = {
    compact(render(Extraction.decompose(d)))
  }
}