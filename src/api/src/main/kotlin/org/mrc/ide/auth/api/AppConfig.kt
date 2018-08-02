package org.mrc.ide.auth.api

import org.mrc.ide.auth.models.ConfigProperties

interface AppConfig {
    val tokenIssuer: String
    val tokenLifespan: Long
    val appPort: Int
}

object AuthApiAppConfig: AppConfig {

    private val configProperties = ConfigProperties(AuthApiAppConfig::class.java.classLoader
            .getResource("config.properties")
            .path)

    override val tokenIssuer = configProperties["token.issuer"]
    override val tokenLifespan = configProperties["token.lifespan"].toLong()
    override val appPort: Int = configProperties.getInt("app.port")
}