package org.mrc.ide.auth.api.routing

import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.security.PermissionRequirement
import org.mrc.ide.auth.security.WebTokenHelper
import spark.route.HttpMethod
import kotlin.reflect.KClass

typealias ResultProcessor = (Any?, ActionContext) -> Any?

interface EndpointDefinition
{
    val urlFragment: String
    val controller: KClass<*>
    val actionName: String
    val method: HttpMethod
    val contentType: String
    val postProcess: ResultProcessor
    val basicAuth: Boolean
    val requiredPermissions: List<PermissionRequirement>
    fun additionalSetup(url: String, webTokenHelper: WebTokenHelper)
}