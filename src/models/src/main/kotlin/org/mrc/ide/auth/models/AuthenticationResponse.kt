package org.mrc.ide.auth.models

import java.time.Duration

interface AuthenticationResponse

class SuccessfulAuthentication(val accessToken: Any, lifeSpan: Duration): AuthenticationResponse
{
    val tokenType = "bearer"
    val expiresIn = lifeSpan.seconds
}

data class FailedAuthentication(val error: String): AuthenticationResponse