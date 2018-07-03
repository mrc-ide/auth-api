package org.mrc.ide.auth.db

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection
import java.sql.DriverManager

open class JooqContext(private val dbName: String? = null) : AutoCloseable
{
    private val conn = getConnection()
    val dsl = createDSL(conn)

    protected open fun loadSettings() = DatabaseSettings.fromConfig()

    private fun getConnection(): Connection
    {
        val config = loadSettings()
        val url = config.url(dbName)
        try
        {
            return DriverManager.getConnection(url, config.username, config.password)
        }
        catch (e: Exception)
        {
            throw UnableToConnectToDatabase(url)
        }
    }

    private fun createDSL(conn: Connection): DSLContext
    {
        return DSL.using(conn, SQLDialect.POSTGRES)
    }

    override fun close()
    {
        conn.close()
    }
}
