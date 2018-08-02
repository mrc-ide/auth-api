package org.mrc.ide.auth.db

import org.mrc.ide.auth.models.ConfigProperties

interface DatabaseConfig {
    val host: String
    val port: String
    val name: String
    val username: String
    val password: String
}

object DatabaseConfigFromConfigProperties : DatabaseConfig {

    private val configProperties = ConfigProperties(DatabaseConfig::class.java.classLoader
            .getResource("config.properties")
            .path)

    override val host = configProperties["db.host"]
    override val port = configProperties["db.port"]
    override val name = configProperties["db.name"]
    override val username = configProperties["db.username"]
    override val password = configProperties["db.password"]

    fun url(name: String? = null): String {
        val dbName = name ?: this.name
        return "jdbc:postgresql://$host:$port/$dbName"
    }
}