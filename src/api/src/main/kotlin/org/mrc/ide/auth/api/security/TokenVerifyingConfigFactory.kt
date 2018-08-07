package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.api.DirectActionContext
import org.pac4j.core.config.Config
import org.pac4j.core.config.ConfigFactory
import org.pac4j.core.context.HttpConstants
import org.pac4j.core.profile.CommonProfile
import org.pac4j.sparkjava.SparkWebContext
import org.mrc.ide.auth.api.errors.MissingRequiredPermissionError
import org.mrc.ide.auth.models.permissions.PermissionSet
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.serialization.models.ErrorInfo

class TokenVerifyingConfigFactory(webTokenHelper: WebTokenHelper,
        private val requiredPermissions: Set<PermissionRequirement>
) : ConfigFactory {

    private val wrappedClients: List<SecurityClientWrapper> = listOf(
            CompressedJWTHeaderClient.Wrapper(webTokenHelper),
            CompressedJWTParameterClient.Wrapper(webTokenHelper, JooqOneTimeTokenChecker())
    )

    private val clients = wrappedClients.map { it.client }

    override fun build(vararg parameters: Any?): Config {
        clients.forEach {
            it.addAuthorizationGenerator { _, profile -> extractPermissionsFromToken(profile) }
        }
        return Config(clients).apply {
            setAuthorizer(ApiAuthorizer(requiredPermissions))
            addMethodMatchers()
            httpActionAdapter = TokenActionAdapter(wrappedClients)
        }
    }

    fun allClients() = clients.joinToString { it::class.java.simpleName }

    private fun extractPermissionsFromToken(profile: CommonProfile): CommonProfile {
        // "permissions" will exists as an attribute because profile is a JwtProfile
        val permissions = PermissionSet((profile.getAttribute("permissions") as String)
                .split(',')
                .filter { it.isNotEmpty() }
        )
        profile.userPermissions = permissions
        return profile
    }
}

class TokenActionAdapter(wrappedClients: List<SecurityClientWrapper>)
    : HttpActionAdapter() {
    private val unauthorizedResponse: List<ErrorInfo> = wrappedClients.map { it.authorizationError }

    private fun forbiddenResponse(missingPermissions: Set<ReifiedPermission>, mismatchedURL: String?): List<ErrorInfo>
    {
        val errors = mutableListOf<ErrorInfo>()
        if (missingPermissions.any())
        {
            errors.addAll(MissingRequiredPermissionError(missingPermissions).problems)
        }
        if (mismatchedURL != null)
        {
            errors.add(ErrorInfo("forbidden", mismatchedURL))
        }
        return errors
    }

    override fun adapt(code: Int, context: SparkWebContext): Any? = when (code)
    {
        HttpConstants.UNAUTHORIZED ->
        {
            haltWithError(code, context, unauthorizedResponse)
        }
        HttpConstants.FORBIDDEN ->
        {
            val profile = DirectActionContext(context).userProfile!!
            haltWithError(code, context, forbiddenResponse(profile.missingPermissions, profile.mismatchedURL))
        }
        else -> super.adapt(code, context)
    }
}