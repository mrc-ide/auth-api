package org.mrc.ide.auth.api

import spark.route.HttpMethod
import kotlin.reflect.KClass

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