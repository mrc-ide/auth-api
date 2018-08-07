package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.security.OneTimeTokenAuthenticator
import org.mrc.ide.auth.security.OneTimeTokenChecker
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.auth.security.inflate
import org.mrc.ide.serialization.models.ErrorInfo
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.credentials.extractor.ParameterExtractor
import org.pac4j.http.client.direct.ParameterClient

// This client receives the token as TokenCredentials and stores the result as JwtProfile
class CompressedJWTParameterClient(helper: WebTokenHelper, oneTimeTokenChecker: OneTimeTokenChecker)
    : ParameterClient("access_token", OneTimeTokenAuthenticator(helper, oneTimeTokenChecker))
{
    init
    {
        @Suppress("UsePropertyAccessSyntax")
        this.setSupportGetRequest(true)
        credentialsExtractor = CompressedParameterExtractor(
                parameterName, isSupportGetRequest, isSupportPostRequest, name)
    }

    class Wrapper(helper: WebTokenHelper, oneTimeTokenChecker: OneTimeTokenChecker): SecurityClientWrapper
    {
        override val client = CompressedJWTParameterClient(helper, oneTimeTokenChecker)
        override val authorizationError = ErrorInfo(
                "onetime-token-invalid",
                "Onetime token not supplied, or onetime token was invalid"
        )
    }
}

class CompressedParameterExtractor(
        parameterName: String,
        supportGetRequest: Boolean,
        supportPostRequest: Boolean,
        clientName: String)
    : ParameterExtractor(parameterName, supportGetRequest, supportPostRequest, clientName)
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
