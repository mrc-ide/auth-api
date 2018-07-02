package org.mrc.ide.auth.security

import org.pac4j.core.profile.CommonProfile
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.mrc.ide.auth.db.Config
import org.mrc.ide.auth.models.Scope
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import java.security.KeyPair
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.*

open class WebTokenHelper(
        keyPair: KeyPair)
{
    open val defaultLifespan: Duration = Duration.ofSeconds(Config["token.lifespan"].toLong())
    val signatureConfiguration = RSASignatureConfiguration(keyPair)
    val generator = JwtGenerator<CommonProfile>(signatureConfiguration)
    val issuer = Config["token.issuer"]
    private val random = SecureRandom()

    open fun generateToken(user: InternalUser, lifeSpan: Duration = defaultLifespan): String
    {
        return generator.generate(claims(user, lifeSpan))
    }

    fun claims(user: InternalUser, lifeSpan: Duration = defaultLifespan): Map<String, Any>
    {
        return mapOf(
                "iss" to issuer,
                "token_type" to TokenType.BEARER,
                "sub" to user.username,
                "exp" to Date.from(Instant.now().plus(lifeSpan)),
                "permissions" to user.permissions.joinToString(","),
                "roles" to user.roles.joinToString(",")
        )
    }

    private fun shinyClaims(user: InternalUser): Map<String, Any>
    {
        val allowedShiny = user.permissions.contains(ReifiedPermission("reports.review", Scope.Global()))
        return mapOf(
                "iss" to issuer,
                "token_type" to TokenType.SHINY,
                "sub" to user.username,
                "exp" to Date.from(Instant.now().plus(defaultLifespan)),
                "allowed_shiny" to allowedShiny.toString()
        )
    }

    open fun verify(compressedToken: String, expectedType: TokenType,
                    oneTimeTokenChecker: OneTimeTokenChecker): Map<String, Any>
    {
        val authenticator = when (expectedType)
        {
            TokenType.ONETIME -> OneTimeTokenAuthenticator(this, oneTimeTokenChecker)
            else -> MontaguTokenAuthenticator(this, expectedType)
        }
        return authenticator.validateTokenAndGetClaims(compressedToken)
    }

    private fun getNonce(): String
    {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    companion object
    {
        val oneTimeActionSubject = "onetime_link"
        val apiResponseSubject = "api_response"
        val oneTimeLinkLifeSpan: Duration = Duration.ofMinutes(10)
    }

}