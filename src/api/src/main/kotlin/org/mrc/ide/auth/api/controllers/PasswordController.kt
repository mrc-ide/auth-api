package org.mrc.ide.auth.api.controllers

import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.db.UserRepository
import org.slf4j.LoggerFactory
import java.time.Duration

//class PasswordController(
//        context: ActionContext,
//        private val userRepository: UserRepository,
//        private val oneTimeTokenGenerator: OneTimeTokenGenerator,
//        private val emailManager: EmailManager = getEmailManager()
//) : Controller(context)
//{
//
//    constructor(context: ActionContext, repositories: Repositories)
//            : this(context, repositories.user, OneTimeTokenGenerator(repositories.token))
//
//    private val logger = LoggerFactory.getLogger(PasswordController::class.java)
//
//    fun setPassword(): String
//    {
//        return setPasswordForUser(context.username!!)
//    }
//
//    fun setPasswordForUser(username: String): String
//    {
//        val password = context.postData<SetPassword>().password
//        userRepository.setPassword(username, password)
//        return okayResponse()
//    }
//
//    fun requestResetPasswordLink(): String
//    {
//        val address = context.queryParams("email")
//                ?: throw MissingRequiredParameterError("email")
//        val internalUser = userRepository.getUserByEmail(address)
//        if (internalUser != null)
//        {
//            val token = getSetPasswordToken(internalUser.username)
//            val email = PasswordSetEmail(token, internalUser.name)
//            emailManager.sendEmail(email, internalUser)
//        }
//        else
//        {
//            logger.warn("Requested set password email for unknown user '$address'")
//        }
//        return okayResponse()
//    }
//
//    private fun getSetPasswordToken(username: String): String
//    {
//        val params = mapOf(":username" to username)
//        return oneTimeTokenGenerator.getOneTimeLinkToken(
//                OneTimeAction.SET_PASSWORD,
//                queryString = null,
//                redirectUrl = null,
//                username = username,
//                params = params,
//                duration = Duration.ofDays(1))
//    }
//
//}