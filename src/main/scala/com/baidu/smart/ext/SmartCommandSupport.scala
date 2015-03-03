package com.baidu.smart.ext

import org.scalatra.ScalatraBase
import org.scalatra.commands.{Command, CommandExecutor, ParamsOnlyCommandSupport}

import scala.language.implicitConversions
import scala.util.{Failure => Fail, Success => Succ, Try}

/**
 * Smart Powerlink
 *
 * @since 14-7-8
 * @author changhai
 */
trait SmartCommandSupport extends ParamsOnlyCommandSupport with ScalatraBase {
  implicit def powerlinkExecutor[T <: Command, S](handle: T => S): CommandExecutor[T, S] =
    new CommandExecutor[T, S](handle: T => S) {
      def execute(cmd: T): S = {
        if (cmd.isValid) {
          val res = Try(handle(cmd))
          res match {
            case Succ(r) ⇒ r
            case Fail(t) ⇒ throw new RuntimeException(t)
          }
        } else {
          val f = cmd.errors.map(_.validation) collect {
            case e ⇒ e
          }
          def failures = if (f.size == 1) "failure" else "failures"
          throw new RuntimeException(failures)
        }
      }
    }
}
