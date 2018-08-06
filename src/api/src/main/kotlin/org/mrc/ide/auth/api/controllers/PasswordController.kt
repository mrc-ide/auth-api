package org.mrc.ide.auth.api.controllers

import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.errors.MissingRequiredParameterError
import org.mrc.ide.auth.api.postData
import org.mrc.ide.auth.db.TokenRepository
import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.emails.EmailManager
import org.mrc.ide.auth.emails.PasswordSetEmail
import org.mrc.ide.auth.models.SetPassword
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.auth.security.deflated
import org.slf4j.LoggerFactory
import java.time.Duration

class PasswordController(
        context: ActionContext,
        private val userRepository: UserRepository,
        private val tokenRepository: TokenRepository,
        private val tokenHelper: WebTokenHelper,
        private val emailManager: EmailManager
) : Controller(context)
{

    private val logger = LoggerFactory.getLogger(PasswordController::class.java)

    fun setPassword(): String
    {
        val username = context.username!!
        val password = context.postData<SetPassword>().password
        userRepository.setPassword(username, password)
        return okayResponse
    }

    fun requestResetPasswordLink(): String
    {
        val address = context.queryParams("email")
                ?: throw MissingRequiredParameterError("email")
        val internalUser = userRepository.getUserByEmail(address)
        if (internalUser != null)
        {
            val token = getSetPasswordToken(internalUser.username)
            val email = PasswordSetEmail(token, internalUser.name)
            emailManager.sendEmail(email, internalUser)
        }
        else
        {
            logger.warn("Requested set password email for unknown user '$address'")
        }
        return okayResponse
    }

    private fun getSetPasswordToken(username: String): String
    {
        val token = tokenHelper.generateSetPasswordToken(Duration.ofDays(1), username)
        tokenRepository.storeToken(token)
        return token.deflated()
    }

}