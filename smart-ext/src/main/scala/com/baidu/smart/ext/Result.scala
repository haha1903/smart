package com.baidu.smart.ext

import org.scalatra.commands.{Field, ParamsOnlyCommand}

abstract class Result {
  var message: Any
  val success: Boolean

  def addMessage(key: String, value: Any) {
    message match {
      case None => Map() + (key -> value)
      case m: Map[String@unchecked, _] => m + (key -> value)
    }
  }

  def addFieldMessage(param: String, msg: String) {
    message match {
      case None => Map() + ("field" -> (Map() + (param -> msg)))
      case m: Map[String@unchecked, _] => m + ("field" -> (m.get("field") match {
        case None => Map() + (param -> m)
        case f: Map[String@unchecked, _] => f + (param -> m)
        case e => e
      }))
    }
  }

  def addGlobalMessage(msg: String) {
    addMessage("global", msg)
  }

  def addNoSessionMessage(msg: String) {
    addMessage("noSession", msg)
  }
}

case class Success[T](result: Any = None, page: Option[Page[T]] = None, var message: Any = None, success: Boolean = true) extends Result

case class Failure(var message: Any = None, success: Boolean = false) extends Result

case class Page[T](result: List[T], pageNo: Int, pageSize: Int, orderBy: String, order: String, totalCount: Int)

case class PageParam(pageSize: Int, pageNo: Int, orderBy: String, order: String) {
  val skip = (pageNo - 1) * pageSize
}
