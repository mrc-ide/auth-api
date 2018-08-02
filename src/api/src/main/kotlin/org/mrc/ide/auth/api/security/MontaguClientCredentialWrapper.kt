package org.mrc.ide.auth.api.security

import org.pac4j.core.client.DirectClient
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.profile.CommonProfile
import org.mrc.ide.serialization.models.ErrorInfo

interface SecurityClientWrapper
{
    val client: DirectClient<TokenCredentials, CommonProfile>
    val authorizationError: ErrorInfo
}
