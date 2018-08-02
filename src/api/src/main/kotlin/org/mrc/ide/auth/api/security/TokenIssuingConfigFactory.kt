package org.mrc.ide.auth.api.security

import org.pac4j.core.config.Config
import org.pac4j.core.config.ConfigFactory
import org.pac4j.core.context.HttpConstants
import org.pac4j.http.client.direct.DirectBasicAuthClient
import org.pac4j.sparkjava.SparkWebContext
import org.mrc.ide.auth.models.FailedAuthentication
import org.mrc.ide.serialization.DefaultSerializer
import org.mrc.ide.serialization.Serializer
import spark.Spark as spk

class TokenIssuingConfigFactory(
        private val serializer: Serializer = DefaultSerializer.instance) : ConfigFactory {
    override fun build(vararg parameters: Any?): Config {
        val authClient = DirectBasicAuthClient(DatabasePasswordAuthenticator())
        return Config(authClient).apply {
            httpActionAdapter = BasicAuthActionAdapter(serializer)
            addMethodMatchers()
        }
    }
}

class BasicAuthActionAdapter(serializer: Serializer)
    : HttpActionAdapter(serializer) {
    private val unauthorizedResponse: String = serializer.gson.toJson(FailedAuthentication("Bad credentials"))

    override fun adapt(code: Int, context: SparkWebContext): Any? = when (code) {
        HttpConstants.UNAUTHORIZED -> {
            context.response.addHeader("WWW-Authenticate", "Basic")
            haltWithError(code, context, unauthorizedResponse)
        }
        else -> super.adapt(code, context)
    }
}
