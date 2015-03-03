package com.baidu.smart.ext

import org.json4s.JsonAST._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Diff}

import scala.language.implicitConversions

/**
 * Copyright (c) 2015 Baidu.com, Inc. All Rights Reserved
 * Author: wangjiayin <wangjiayin@baidu.com>
 * CreateDate: 2015/2/6 11:36
 * Description: Json4s Utils
 */
trait Json4sSupport {

  implicit val formats = DefaultFormats

  def fromJson[T](json: String)(implicit mf: Manifest[T]): T = {
    parse(json).extract[T]
  }

  implicit def Any2JValue(a: Any): JValue = {
    a match {
      case a: Int => JInt(a)
      case a: Double => JDouble(a)
      case a: Boolean => JBool(a)
      case a: String => JString(a)
      case null => JNull
      case _ => JString(a.toString)
    }
  }

  def toJson(map: Map[String, Any]) = {
    compact(render(map))
  }

  def toJson(v: JValue) = {
    compact(render(v))
  }

  /**
   * 比较两个json字符串代表的数据是否相同
   * @param json1
   * @param json2
   * @return
   */
  def jsonEquals(json1: String, json2: String) = {
    parse(json1) == parse(json2)
  }

  /**
   * 将两个json字符串合并，返回合并后的json字符串
   * @param json1
   * @param json2
   * @return
   */
  def jsonMerge(json1: String, json2: String): String = {
    val merged = parse(json1) merge parse(json2)
    pretty(render(merged))
  }

  /**
   * 比较两个json字符串，返回三元组（改，增，删）
   * @param json1
   * @param json2
   * @return
   */
  def jsonDiff(json1: String, json2: String): (String, String, String) = {
    val Diff(changed, added, deleted) = parse(json1) diff parse(json2)
    (pretty(render(changed)), pretty(render(added)), pretty(render(deleted)))
  }
}

//object Test extends Json4sSupport {
//  def main(args: Array[String]) {
//    val lotto1 = parse( """{
//         "lotto":{
//           "lotto-id":5,
//           "winning-numbers":[2,45,34,23,7,5,3]
//           "winners":[{
//             "winner-id":23,
//             "numbers":[2,45,34,23,3,5]
//           }]
//         }
//       }""")
//
//    val lotto2 = parse( """{
//         "lotto":{
//           "winners":[{
//             "winner-id":54,
//             "numbers":[52,3,12,11,18,22]
//           }]
//         }
//       }""")
//
//    val mergedLotto = lotto1 merge lotto2
//    println(pretty(render(mergedLotto)))
//    val Diff(changed, added, deleted) = mergedLotto diff lotto1
//    println(changed)
//    println(added)
//    println(pretty(render(deleted)))
//
//
//    println(jsonEquals( """{"key1":1, "key2":2}""", """{"key2":2, "key1":1}"""))
//
//  }
//}