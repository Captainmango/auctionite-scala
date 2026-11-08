package common

import play.api.db.{DBApi, Database, TransactionIsolationLevel}

import javax.inject.{Inject, Singleton}
import javax.sql.DataSource

import java.lang.reflect.{Method, Proxy}
import java.sql.Connection

/** A [[Database]] that funnels every caller through ONE auto-commit-off connection, so all database work in a test
  * joins a single transaction that [[rollback]] can discard.
  *
  * The connection handed out is a proxy whose `close`, `commit` and `setAutoCommit` are no-ops, so neither
  * `withConnection`-style usage nor `withTransaction` in code under test can end the test's transaction. The real
  * connection is kept private and is only closed by [[rollback]], which returns it to the pool.
  *
  * This is the binding installed by the `TransactionalTests` trait; it is not meant to be instantiated by hand.
  */
@Singleton
class TransactionalDatabase @Inject() (dbApi: DBApi) extends Database:

  private val real: Database = dbApi.database("default")

  private val transactional: Connection =
    val conn = real.getConnection()
    conn.setAutoCommit(false)
    conn

  private val shared: Connection = TransactionalDatabase.protect(transactional)

  override def name: String = real.name
  override def dataSource: DataSource = real.dataSource
  override def url: String = real.url
  override def getConnection(): Connection = shared
  override def getConnection(autocommit: Boolean): Connection = shared

  override def withConnection[A](block: Connection => A): A = block(shared)
  override def withConnection[A](autocommit: Boolean)(block: Connection => A): A = block(shared)

  /** There is no inner transaction to commit: callers join the test-wide one, and it all rolls back together. */
  override def withTransaction[A](block: Connection => A): A = block(shared)
  override def withTransaction[A](isolationLevel: TransactionIsolationLevel)(block: Connection => A): A =
    block(shared)

  override def shutdown(): Unit = rollback()

  /** Discards everything written through this database and returns the connection to the real pool. Safe to call more
    * than once.
    */
  def rollback(): Unit =
    if !transactional.isClosed then
      try
        transactional.rollback()
        transactional.setAutoCommit(true)
      finally transactional.close()

object TransactionalDatabase:

  /** Wraps a connection so `close`, `commit` and `setAutoCommit` never reach it; everything else is forwarded. */
  private def protect(conn: Connection): Connection =
    Proxy
      .newProxyInstance(
        classOf[Connection].getClassLoader,
        Array(classOf[Connection]),
        (_, method: Method, args: Array[AnyRef]) =>
          method.getName match
            case "close" | "commit" | "setAutoCommit" => null
            case _ if args == null                    => method.invoke(conn)
            case _                                    => method.invoke(conn, args*)
      )
      .asInstanceOf[Connection]
