package org.mrc.ide.auth.api.routing

import org.mrc.ide.auth.api.controllers.AuthenticationController
import org.mrc.ide.auth.api.controllers.PasswordController
import spark.route.HttpMethod

interface RouteConfig {
    val endpoints: List<EndpointDefinition>
}

object PasswordRouteConfig : RouteConfig {
    private val controller = PasswordController::class
    private const val urlBase = "/password"

    override val endpoints: List<EndpointDefinition> = listOf(
            Endpoint("$urlBase/set/", controller, "setPassword")
                    .json()
                    .post()
                    .secure(),
            Endpoint("$urlBase/request-link/", controller, "requestResetPasswordLink")
                    .post()
                    .json()
    )
}


object AuthenticationRouteConfig : RouteConfig {
    private val controller = AuthenticationController::class

    override val endpoints: List<EndpointDefinition> = listOf(
            Endpoint("/authenticate/", controller, "authenticate", method = HttpMethod.post)
                    .json()
                    .basicAuth()
    )
}

object ApiRouteConfig : RouteConfig {
    override val endpoints: List<EndpointDefinition> =
            PasswordRouteConfig.endpoints +
                    AuthenticationRouteConfig.endpoints
}

