package org.mrc.ide.auth.security

import com.nimbusds.jwt.JWT
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.exception.CredentialsException

class OneTimeTokenAuthenticator(
        tokenHelper: WebTokenHelper,
        private val oneTimeTokenChecker: OneTimeTokenChecker
) : CompressedTokenAuthenticator(tokenHelper, TokenType.ONETIME)
{
    override fun createJwtProfile(credentials: TokenCredentials, jwt: JWT)
    {
        super.createJwtProfile(credentials, jwt)
        checkTokenAgainstRepository(credentials)
    }

    private fun checkTokenAgainstRepository(credentials: TokenCredentials)
    {
        val compressedToken = credentials.token
        if (!oneTimeTokenChecker.checkToken(compressedToken.inflated()))
        {
            throw CredentialsException("Token has already been used (or never existed)")
        }
    }
}
