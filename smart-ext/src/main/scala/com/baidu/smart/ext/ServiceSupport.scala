package com.baidu.smart.ext

import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.scalatra.{RouteTransformer, ScalatraBase, Get, Post}

/**
 * Smart Powerlink
 *
 * @since 14-7-21
 * @author changhai
 */
trait ServiceSupport extends ScalatraBase with SwaggerSupport {
  implicit val swagger: Swagger

  override protected def applicationDescription: String = "Smart Powerlink"

  def service(transformers: RouteTransformer*)(action: => Any) = {
    val (get, post) = transformers match {
      case Seq(t) => (Seq(t), Seq(t, operation(apiOperation[Any](t.toString))))
      case Seq(t1, t2) => (Seq(t1), Seq(t1, t2))
      case t => (t, t)
    }
    addRoute(Get, get, action)
    addRoute(Post, post, action)
  }
}
