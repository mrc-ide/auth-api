package org.mrc.ide.auth.api

import org.mrc.ide.auth.models.ConfigProperties

object AppConfig {

    private val configProperties = ConfigProperties(AppConfig::class.java.classLoader
            .getResource("config.properties")
            .path)

    val tokenIssuer = configProperties["token.issuer"]
    val tokenLifespan = configProperties["token.lifespan"].toLong()
    val appPort: Int = configProperties.getInt("app.port")
    val url: String = configProperties["app.url"]
}