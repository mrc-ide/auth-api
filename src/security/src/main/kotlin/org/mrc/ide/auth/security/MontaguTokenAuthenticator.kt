package org.mrc.ide.auth.security

import com.nimbusds.jwt.JWT
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.profile.CommonProfile
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator

open class MontaguTokenAuthenticator(
        private val tokenHelper: WebTokenHelper,
        private val expectedType: TokenType
) : JwtAuthenticator(tokenHelper.signatureConfiguration)
{
    override fun validateToken(compressedToken: String?): CommonProfile?
    {
        return super.validateToken(inflate(compressedToken))
    }

    override fun createJwtProfile(credentials: TokenCredentials, jwt: JWT)
    {
        super.createJwtProfile(credentials, jwt)
        val claims = jwt.jwtClaimsSet
        val issuer = claims.issuer
        if (issuer != tokenHelper.issuer)
        {
            throw CredentialsException("Token was issued by '$issuer'. Must be issued by '${tokenHelper.issuer}'")
        }
        val tokenType = claims.getClaim("token_type").toString()
        if (tokenType != expectedType.toString())
        {
            throw CredentialsException("Wrong type of token was provided. " +
                    "Expected '$expectedType', was actually '$tokenType'")
        }
        handleUrlAttribute(credentials, jwt)
    }

    protected open fun handleUrlAttribute(credentials: TokenCredentials, jwt: JWT)
    {
        // OAuth access tokens are allowed to access any URL
        credentials.userProfile?.addAttribute("url", "*")
    }
}