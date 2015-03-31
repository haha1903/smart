package com.scalaone.smart.ext

import java.text.SimpleDateFormat

import org.json4s.{DefaultFormats, FieldSerializer}
import org.scalatra.RenderPipeline
import org.scalatra.json.{JValueResult, JsonSupport}

/**
 * Smart Powerlink
 *
 * @since 14-4-24
 * @author changhai
 */
trait SmartValueResult extends JValueResult {
  self: JsonSupport[_] =>
  override protected val jsonFormats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss")

    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = List((classOf[Object], new FieldSerializer[Object]))
  }

  before() {
    contentType = formats("json")
  }

  override protected def renderPipeline: RenderPipeline = renderToJson orElse super.renderPipeline

  override protected def renderResponseBody(actionResult: Any) {
    actionResult match {
      case _: Unit | Unit => super.renderResponseBody("")
      case _ => super.renderResponseBody(actionResult)
    }
  }

  private[this] def isJValueResponse = format == "json" || format == "xml"

  private[this] def renderToJson: RenderPipeline = {
    case p: Page[_] if isJValueResponse => Success(page = Some(p))
    case p: Any if !p.isInstanceOf[Result] && isJValueResponse => Success(p)
  }
}
