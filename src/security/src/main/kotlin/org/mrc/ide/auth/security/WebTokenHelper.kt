package org.mrc.ide.auth.security

import org.mrc.ide.auth.models.ConfigWrapper
import org.pac4j.core.profile.CommonProfile
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.mrc.ide.auth.models.User
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.util.*

open class WebTokenHelper(config: ConfigWrapper,
                          keyPair: KeyPair)
{
    open val defaultLifespan: Duration = Duration.ofSeconds(config["token.lifespan"].toLong())
    val signatureConfiguration = RSASignatureConfiguration(keyPair)
    val generator = JwtGenerator<CommonProfile>(signatureConfiguration)
    val issuer = config["token.issuer"]

    open fun generateToken(user: User, lifeSpan: Duration = defaultLifespan): String
    {
        return generator.generate(claims(user, lifeSpan))
    }

    fun claims(user: User, lifeSpan: Duration = defaultLifespan): Map<String, Any>
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

}