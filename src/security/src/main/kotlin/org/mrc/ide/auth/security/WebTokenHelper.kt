package org.mrc.ide.auth.security

import org.mrc.ide.auth.models.User
import org.pac4j.core.profile.CommonProfile
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import java.security.KeyPair
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.*

enum class TokenType {
    BEARER,
    ONETIME
}

open class WebTokenHelper(val issuer: String,
                          tokenLifespan: Long,
                          keyPair: KeyPair) {
    open val defaultLifespan: Duration = Duration.ofSeconds(tokenLifespan)
    val signatureConfiguration = RSASignatureConfiguration(keyPair)
    val generator = JwtGenerator<CommonProfile>(signatureConfiguration)
    private val random = SecureRandom()

    open fun generateToken(user: User, lifeSpan: Duration = defaultLifespan): String {
        return generator.generate(claims(user, lifeSpan))
    }

    open fun generateSetPasswordToken(
            lifeSpan: Duration,
            username: String): String {

        val params = mapOf(":username" to username)

        return generator.generate(mapOf(
                "iss" to issuer,
                "sub" to "SET_PASSWORD",
                "exp" to Date.from(Instant.now().plus(lifeSpan)),
                "token_type" to TokenType.ONETIME,
                "payload" to params.map { "${it.key}=${it.value}" }.joinToString("&"),
                "nonce" to getNonce(),
                "username" to username
        ))
    }

    fun claims(user: User, lifeSpan: Duration = defaultLifespan): Map<String, Any> {
        return mapOf(
                "iss" to issuer,
                "sub" to user.username,
                "exp" to Date.from(Instant.now().plus(lifeSpan)),
                "permissions" to user.permissions.joinToString(","),
                "token_type" to TokenType.BEARER,
                "roles" to user.roles.joinToString(","),
                "nonce" to getNonce()
        )
    }

    private fun getNonce(): String {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

}