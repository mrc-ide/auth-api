package org.mrc.ide.auth.api

import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.models.AuthenticationResponse
import org.mrc.ide.auth.models.FailedAuthentication
import org.mrc.ide.auth.models.SuccessfulAuthentication
import org.mrc.ide.auth.security.WebTokenHelper


class AuthController(private val context: ActionContext,
                     private val userRepository: UserRepository,
                     private val tokenHelper: WebTokenHelper,
                     private val htmlFormHelpers: FormHelpers = HTMLFormHelpers()) {

    fun authenticate(): AuthenticationResponse {
        val validationResult = htmlFormHelpers.checkForm(context,
                mapOf("grant_type" to "client_credentials")
        )
        return when (validationResult) {
            is HTMLForm.ValidForm -> {
                val user = context.userProfile!!.user!!
                val token = tokenHelper.generateToken(user)
                userRepository.updateLastLoggedIn(user.username)
                return SuccessfulAuthentication(token, tokenHelper.defaultLifespan)
            }
            is HTMLForm.InvalidForm -> FailedAuthentication(validationResult.problem)
        }
    }
}