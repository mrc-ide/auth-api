package org.mrc.ide.auth.db

import org.mrc.ide.auth.models.Config

object DatabaseConfig : Config(DatabaseConfig::class.java.classLoader
        .getResource("config.properties")
        .path)

data class DatabaseSettings(
        val host: String,
        val port: String,
        val name: String,
        val username: String,
        val password: String
)
{
    fun url(name: String? = null): String
    {
        val dbName = name ?: this.name
        return "jdbc:postgresql://$host:$port/$dbName"
    }

    companion object
    {
        fun fromConfig() = DatabaseSettings(
                DatabaseConfig["db.host"],
                DatabaseConfig["db.port"],
                DatabaseConfig["db.name"],
                DatabaseConfig["db.username"],
                DatabaseConfig["db.password"]
        )
    }
}