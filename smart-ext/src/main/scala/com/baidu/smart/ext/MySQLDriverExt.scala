package com.baidu.smart.ext

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.baidu.smart.ext.Sharding._

import scala.language.implicitConversions
import scala.slick.SlickException
import scala.slick.ast.{FieldSymbol, Insert, Node, TableNode}
import scala.slick.compiler.CodeGen
import scala.slick.driver.{InsertBuilderResult, JdbcProfile, MySQLDriver}
import scala.slick.lifted.{Query, TableQuery}
import scala.slick.profile.Capability
import scala.slick.util.SQLBuilder

trait MySQLDriverExt extends MySQLDriver {

  protected override def computeCapabilities: Set[Capability] = super.computeCapabilities - JdbcProfile.capabilities.insertOrUpdate

  override def createInsertBuilder(node: Insert): InsertBuilder = new InsertBuilderWithValue(node)

  override def createInsertInvoker[U](compiled: CompiledInsert): CountingInsertInvoker[U] = createShardingCountingInsertInvoker(compiled)

  class JdbcTypes extends super.JdbcTypes {
    override val timestampJdbcType = new TimestampJdbcType

    class TimestampJdbcType extends super.TimestampJdbcType {
      private val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

      override def valueToSQLLiteral(value: Timestamp) = "'" + format.format(value) + "'"
    }

  }

  override val simple = new SimpleQL with Implicits {

    implicit def tableQueryToTableQueryExtensionMethodsExt[T <: Table[_], U](q: Query[T, U, Seq] with TableQuery[T]) =
      new TableQueryExtensionMethodsExt[T, U](q)

    implicit def ddlToDDLInvokerExt(d: DDLExt): DDLInvokerExt = new DDLInvokerExt(d)
  }

  class TableQueryExtensionMethodsExt[T <: Table[_], U](val q: Query[T, U, Seq] with TableQuery[T]) {
    def ddlext: DDLExt = new TableDDLBuilderExt(q.baseTableRow).buildDDL
  }

  def createShardingCountingInsertInvoker[U](compiled: CompiledInsert) = new ShardingCountingInsertInvoke[U](compiled)

  class TableDDLBuilderExt(table: Table[_]) extends TableDDLBuilder(table) {
    override def buildDDL: DDLExt = {
      if (primaryKeys.size > 1)
        throw new SlickException("Table " + tableNode.tableName + " defines multiple primary keys ("
          + primaryKeys.map(_.name).mkString(", ") + ")")
      DDLExt(createPhase1, createPhase2, dropPhase1, dropPhase2, truncate)
    }

    private def shardingTableNode(q: TableNode) = {
      parse(tableNode.tableName) match {
        case Some(mod) => 0.until(mod.mod).map(i => tableNode.copy(tableName = mod.prefix + i))
        case None => List(tableNode)
      }
    }

    protected def truncate = truncateTable

    protected def truncateTable: Iterable[String] = shardingTableNode(tableNode).map(q => new StringBuilder().append("truncate table ").append(quoteTableName(q)).toString)
  }

  class DDLInvokerExt(ddl: DDLExt) extends DDLInvoker(ddl) {
    def truncate(implicit session: Backend#Session): Unit = session.withTransaction {
      for (s <- ddl.truncateStatements)
        session.withPreparedStatement(s)(_.execute)
    }
  }

  trait DDLExt extends DDL {
    self =>
    protected def truncate: Iterable[String]

    protected def createPhase1: Iterable[String]

    protected def createPhase2: Iterable[String]

    protected def dropPhase1: Iterable[String]

    protected def dropPhase2: Iterable[String]

    def truncateStatements: Iterator[String] = truncate.iterator

    override def ++(other: DDL): DDLExt = new DDLExt {
      private def otherExt = other.asInstanceOf[DDLExt]

      protected lazy val createPhase1 = self.createPhase1 ++ otherExt.createPhase1
      protected lazy val createPhase2 = self.createPhase2 ++ otherExt.createPhase2
      protected lazy val dropPhase1 = self.dropPhase1 ++ otherExt.dropPhase1
      protected lazy val dropPhase2 = self.dropPhase2 ++ otherExt.dropPhase2
      protected lazy val truncate = self.truncate ++ otherExt.truncate
    }
  }

  object DDLExt {
    def apply(create1: Iterable[String], create2: Iterable[String], drop1: Iterable[String],
              drop2: Iterable[String], t: Iterable[String]) = new DDLExt {
      protected def createPhase1 = create1

      protected def createPhase2 = create2

      protected def dropPhase1 = drop1

      protected def dropPhase2 = drop2

      protected def truncate = t
    }
  }

  class InsertBuilderResultExt(val ib: InsertBuilderWithValue, val t: TableNode, val s: String, val f: IndexedSeq[FieldSymbol]) extends InsertBuilderResult(t, s, f)

  class InsertBuilderWithValue(insert: Insert) extends InsertBuilder(insert) {
    private def tableNameWithValue[U](value: U) = {
      val t = parse(table.tableName) match {
        case Some(mod) => table.copy(tableName = mod.prefix + getValue(value, mod.field).asInstanceOf[Long] % mod.mod)
        case None => table
      }
      quoteTableName(t)
    }

    private def buildInsertStartWithValue[U](value: U): String = {
      val tableName = tableNameWithValue(value)
      allNames.mkString(s"insert into $tableName (", ",", ") ")
    }

    override def buildInsert: InsertBuilderResult = {
      parse(table.tableName) match {
        case Some(mod) => new InsertBuilderResultExt(this, table, "", syms)
        case _ => val start = buildInsertStart
          new InsertBuilderResult(table, s"$start values $allVars", syms) {
            override def buildInsert(compiledQuery: Node) = {
              val (_, sbr: SQLBuilder.Result) = CodeGen.findResult(compiledQuery)
              SQLBuilder.Result(start + sbr.sql, sbr.setter)
            }
          }
      }
    }

    def buildSql[U](value: U) = {
      val start = buildInsertStartWithValue(value)
      s"$start values $allVars"
    }
  }

  class ShardingCountingInsertInvoke[U](tree: CompiledInsert) extends CountingInsertInvoker[U](tree) {
    override protected def internalInsert(a: compiled.Artifacts, value: U)(implicit session: Backend#Session): SingleInsertResult = {
      val sql = a.ibr match {
        case re: InsertBuilderResultExt => re.ib.buildSql(value)
        case _ => a.sql
      }
      preparedInsert(sql) { st =>
        st.clearParameters()
        a.converter.set(value, st)
        val count = st.executeUpdate()
        retOne(st, value, count)
      }
    }
  }

}

object MySQLDriverExt extends MySQLDriverExt