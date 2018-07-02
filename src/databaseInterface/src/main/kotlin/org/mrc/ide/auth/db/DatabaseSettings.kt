package org.mrc.ide.auth.db

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
        fun fromConfig(prefix: String) = DatabaseSettings(
                Config["$prefix.host"],
                Config["$prefix.port"],
                Config["$prefix.name"],
                Config["$prefix.username"],
                Config["$prefix.password"]
        )
    }
}