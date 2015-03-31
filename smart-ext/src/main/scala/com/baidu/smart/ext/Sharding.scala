package com.scalaone.smart.ext

import scala.language.experimental.macros
import scala.reflect.macros.blackbox._
import scala.slick.ast._
import scala.slick.lifted._
import scala.util.parsing.combinator.RegexParsers

object Sharding {
  def getValue[T](target: T, name: String) = target.getClass.getDeclaredMethod(name).invoke(target)

  case class Mod(prefix: String, field: String, mod: Int)

  object ModParser extends RegexParsers {
    val number = "[0-9]+".r
    val c = """[\w_]+""".r

    def mod: Parser[Mod] = c ~ "${" ~ c ~ "%" ~ number ~ "}" ^^ { case prefix ~ "${" ~ field ~ "%" ~ mod ~ "}" =>
      Mod(prefix, field, mod.toInt)
    }
  }

  def parse(tableName: String) = {
    val result = ModParser.parse(ModParser.mod, tableName)
    if (result.successful) Some(result.get) else None
  }
}

import com.scalaone.smart.ext.Sharding._

class TableQueryExt[E <: AbstractTable[_]](cons: Tag => E) extends TableQuery[E](cons) {
  override def withFilter[T: CanBeQueryCondition](f: E => T) = filterHelper(f, identity)

  private def filterHelper[T](f: E => T, wrapExpr: Node => Node)
                             (implicit wt: CanBeQueryCondition[T]): scala.slick.lifted.Query[E, E#TableElementType, Seq] = {
    val generator = new AnonSymbol
    val aliased = shaped.encodeRef(generator :: Nil)
    val fv = f(aliased.value)

    val node = try {
      val (fieldName, value: Long) = fv match {
        case c: scala.slick.lifted.Column[_] => c.toNode.nodeChildren.toList match {
          case Select(_, FieldSymbol(fieldName)) :: List(LiteralNode(value@(_: Long | _: Int))) => (fieldName, value)
        }
      }
      val tableName = aliased.value.tableName
      val result = parse(tableName)
      result match {
        case Some(mod) =>
          val columnName = getValue(aliased.value, mod.field) match {
            case c: scala.slick.lifted.Column[_] => c.toNode match {
              case Select(_, FieldSymbol(name)) => name
              case _ => null
            }
            case _ => null
          }
          if (fieldName == columnName) {
            val newTableName = mod.prefix + (value % mod.mod)
            toNode match {
              case TableExpansion(g, t: TableNode, columns) => TableExpansion(g, t.copy(tableName = newTableName), columns)
              case _ => toNode
            }
          } else {
            toNode
          }
        case None => toNode
      }
    } catch {
      case e: MatchError => toNode
    }
    new WrappingQuery[E, E#TableElementType, Seq](Filter.ifRefutable(generator, node, wrapExpr(wt(fv).toNode)), shaped)
  }
}

object TableQueryExt {
  /** Create a TableQuery for a table row class using an arbitrary constructor function. */
  def apply[E <: AbstractTable[_]](cons: Tag => E): TableQuery[E] = new TableQueryExt[E](cons)

  /** Create a TableQuery for a table row class which has a constructor of type (Tag). */
  def apply[E <: AbstractTable[_]]: TableQuery[E] = macro TableQueryExtMacroImpl.apply[E]
}

object TableQueryExtMacroImpl {

  def apply[E <: AbstractTable[_]](c: Context)(implicit e: c.WeakTypeTag[E]): c.Expr[TableQuery[E]] = {
    import c.universe._
    val cons = c.Expr[Tag => E](Function(
      List(ValDef(Modifiers(Flag.PARAM), TermName("tag"), Ident(typeOf[Tag].typeSymbol), EmptyTree)),
      Apply(
        Select(New(TypeTree(e.tpe)), termNames.CONSTRUCTOR),
        List(Ident(TermName("tag")))
      )
    ))
    reify {
      TableQueryExt.apply[E](cons.splice)
    }
  }
}
