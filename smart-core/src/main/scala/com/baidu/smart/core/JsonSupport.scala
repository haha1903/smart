package com.baidu.smart.core

import org.json4s.{JsonInput, Formats, Extraction}
import org.json4s.jackson.JsonMethods._

/**
 * Created by changhai on 15/3/16.
 */
trait JsonSupport {
  def parseJson[T](json: JsonInput, useBigDecimalForDouble: Boolean = false)(implicit formats: Formats, mf: Manifest[T]) = {
    parse(json, useBigDecimalForDouble).extract[T]
  }

  def toJson(d: Any)(implicit formats: Formats) = {
    compact(render(Extraction.decompose(d)))
  }
}
