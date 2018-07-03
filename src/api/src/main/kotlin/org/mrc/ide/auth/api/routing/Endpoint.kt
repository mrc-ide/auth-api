package org.mrc.ide.auth.api.routing

import org.mrc.ide.auth.api.ActionContext
import spark.route.HttpMethod

object ContentTypes
{
    val csv = "text/csv"
    val json = "application/json"
}

data class Endpoint(
        override val urlFragment: String,
        override val actionName: String,
        override val contentType: String = ContentTypes.json,
        override val method: HttpMethod = HttpMethod.get,
        override val postProcess: ResultProcessor = ::passThrough,
        override val basicAuth: Boolean = false

) : EndpointDefinition
{
    init
    {
        if (!urlFragment.endsWith("/"))
        {
            throw Exception("All endpoint definitions must end with a forward slash: $urlFragment")
        }
    }

}

// This means that the endpoint will return JSON data, and we will only respond to requests
// that say they accept application/json
fun Endpoint.json(): Endpoint
{
    return this.copy(contentType = ContentTypes.json)
}

fun Endpoint.basicAuth(): Endpoint
{
    return this.copy(basicAuth = true)
}

fun Endpoint.post(): Endpoint
{
    return this.copy(method = spark.route.HttpMethod.post)
}

private fun passThrough(x: Any?, @Suppress("UNUSED_PARAMETER") context: ActionContext): Any? = x
