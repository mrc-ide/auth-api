package org.mrc.ide.auth.api.routing

import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.DefaultHeadersFilter
import org.mrc.ide.auth.api.security.ApiAuthorizer
import org.mrc.ide.auth.api.security.PermissionRequirement
import org.mrc.ide.auth.api.security.TokenIssuingConfigFactory
import org.mrc.ide.auth.api.security.TokenVerifyingConfigFactory
import org.mrc.ide.auth.security.OneTimeTokenChecker
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.serialization.models.ContentTypes
import org.pac4j.http.client.direct.DirectBasicAuthClient
import org.pac4j.sparkjava.SecurityFilter
import spark.Spark
import spark.route.HttpMethod
import kotlin.reflect.KClass

data class Endpoint(
        override val urlFragment: String,
        override val controller: KClass<*>,
        override val actionName: String,
        override val contentType: String = ContentTypes.json,
        override val method: HttpMethod = HttpMethod.get,
        override val postProcess: ResultProcessor = ::passThrough,
        override val requiredPermissions: List<PermissionRequirement> = listOf(),
        override val basicAuth: Boolean = false

) : EndpointDefinition {

    init {
        if (!urlFragment.endsWith("/")) {
            throw Exception("All endpoint definitions must end with a forward slash: $urlFragment")
        }
    }


    override fun additionalSetup(url: String, webTokenHelper: WebTokenHelper) {
        if (requiredPermissions.any()) {
            addSecurityFilter(url, webTokenHelper)
        }
        if (basicAuth) {
            setupSecurity(url)
        }

        Spark.after(url, contentType, DefaultHeadersFilter("$contentType; charset=utf-8", method))
    }

    private fun addSecurityFilter(url: String, webTokenHelper: WebTokenHelper) {

        val configFactory = TokenVerifyingConfigFactory(webTokenHelper,
                requiredPermissions.toSet())

        val config = configFactory.build()

        Spark.before(url, org.pac4j.sparkjava.SecurityFilter(
                config,
                configFactory.allClients(),
                ApiAuthorizer::class.java.simpleName,
                "method:$method"
        ))

    }

    private fun setupSecurity(url: String) {

        val config = TokenIssuingConfigFactory().build()
        Spark.before(url, SecurityFilter(
                config,
                DirectBasicAuthClient::class.java.simpleName,
                null,
                "method:${HttpMethod.post}"
        ))

    }

}

fun Endpoint.secure(permissions: Set<String> = setOf()): Endpoint {
    val allPermissions = (permissions + "*/can-login").map {
        PermissionRequirement.parse(it)
    }
    return this.copy(requiredPermissions = allPermissions)
}

// This means that the endpoint will return JSON data, and we will only respond to requests
// that say they accept application/json
fun Endpoint.json(): Endpoint {
    return this.copy(contentType = ContentTypes.json)
}

fun Endpoint.basicAuth(): Endpoint {
    return this.copy(basicAuth = true)
}

fun Endpoint.post(): Endpoint {
    return this.copy(method = spark.route.HttpMethod.post)
}

private fun passThrough(x: Any?, @Suppress("UNUSED_PARAMETER") context: ActionContext): Any? = x
