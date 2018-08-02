package org.mrc.ide.auth.api.security

import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.credentials.extractor.HeaderExtractor
import org.pac4j.http.client.direct.HeaderClient
import org.mrc.ide.auth.security.CompressedTokenAuthenticator
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.auth.security.inflate
import org.mrc.ide.serialization.models.ErrorInfo

// This client receives the token as TokenCredentials and stores the result as JwtProfile
class CompressedJWTHeaderClient(webTokenHelper: WebTokenHelper)
    : HeaderClient("Authorization", "Bearer ", CompressedTokenAuthenticator(webTokenHelper))
{
    init
    {
        credentialsExtractor = CompressedHeaderExtractor(headerName, prefixHeader, name)
    }

    class Wrapper(webTokenHelper: WebTokenHelper) : SecurityClientWrapper
    {
        override val client = CompressedJWTHeaderClient(webTokenHelper)
        override val authorizationError = ErrorInfo(
                "bearer-token-invalid",
                "Bearer token not supplied in Authorization header, or bearer token was invalid"
        )
    }
}

class CompressedHeaderExtractor(headerName: String, prefixHeader: String, name: String)
    : HeaderExtractor(headerName, prefixHeader, name)
{
    override fun extract(context: WebContext?): TokenCredentials?
    {
        val wrapped = super.extract(context)
        return if (wrapped != null)
        {
            TokenCredentials(inflate(wrapped.token), wrapped.clientName)
        }
        else null
    }
}