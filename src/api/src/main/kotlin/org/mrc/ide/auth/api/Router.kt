package org.mrc.ide.auth.api

import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.security.WebTokenHelper
import org.slf4j.LoggerFactory
import spark.Route
import spark.Spark
import spark.route.HttpMethod
import java.lang.reflect.InvocationTargetException

class Router(private val serializer: Serializer,
             private val webTokenHelper: WebTokenHelper) {

    private val logger = LoggerFactory.getLogger(Router::class.java)

    private val endpoints: List<EndpointDefinition> = listOf(
            Endpoint("/authenticate/", "authenticate", method = HttpMethod.post)
                    .json()
                    .basicAuth())

    companion object
    {
        val urls: MutableList<String> = mutableListOf()
    }

    fun mapEndpoints(urlBase: String): List<String>
    {
        urls.addAll(endpoints
                .sortedBy { it.urlFragment }
                .map { mapEndpoint(it, urlBase) }
        )
        return urls
    }

    private fun transform(x: Any?) = serializer.toJson(x)

    private fun mapEndpoint(
            endpoint: EndpointDefinition,
            urlBase: String): String {
        val fullUrl = urlBase + endpoint.urlFragment
        val route = getWrappedRoute(endpoint)::handle
        val contentType = endpoint.contentType

        logger.info("Mapping $fullUrl to ${endpoint.actionName} on AuthController")
        when (endpoint.method) {
            HttpMethod.get -> Spark.get(fullUrl, contentType, route, this::transform)
            HttpMethod.post -> Spark.post(fullUrl, contentType, route, this::transform)
            HttpMethod.put -> Spark.put(fullUrl, contentType, route, this::transform)
            HttpMethod.patch -> Spark.patch(fullUrl, contentType, route, this::transform)
            HttpMethod.delete -> Spark.delete(fullUrl, contentType, route, this::transform)
            else -> throw Exception("Unsupported method ${endpoint.method.name}")
        }

        return fullUrl
    }

    private fun getWrappedRoute(endpoint: EndpointDefinition): Route {
        return Route({ req, res ->
            invokeControllerAction(endpoint, DirectActionContext(req, res))
        })
    }

    private fun invokeControllerAction(endpoint: EndpointDefinition, context: ActionContext): Any? {
        val actionName = endpoint.actionName
        val controller = AuthController(context, UserRepository(JooqContext().dsl), webTokenHelper)
        val action = AuthController::class.java.getMethod(actionName)

        val result = try {
            action.invoke(controller)
        } catch (e: InvocationTargetException) {
            logger.warn("Exception was thrown whilst using reflection to invoke " +
                    "AuthController.$actionName, see below for details")
            throw e.targetException
        }
        return endpoint.postProcess(result, context)
    }

}