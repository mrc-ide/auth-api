package org.mrc.ide.auth.api.routing

import org.mrc.ide.auth.api.ActionContext
import spark.route.HttpMethod

typealias ResultProcessor = (Any?, ActionContext) -> Any?

interface EndpointDefinition
{
    val urlFragment: String
    val actionName: String
    val method: HttpMethod
    val contentType: String
    val postProcess: ResultProcessor
    val basicAuth: Boolean
}