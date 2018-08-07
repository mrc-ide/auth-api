package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.JooqUserRepository
import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.security.SodiumPasswordEncoder
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.password.PasswordEncoder
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.util.CommonHelper

class DatabasePasswordAuthenticator(val passwordEncoder: PasswordEncoder = SodiumPasswordEncoder())
    : Authenticator<UsernamePasswordCredentials>
{
    override fun validate(credentials: UsernamePasswordCredentials?, context: WebContext?)
    {
        if (credentials == null)
        {
            throwsException("No credentials supplied")
        }
        else
        {
            val email = credentials.username
            val password = credentials.password
            if (CommonHelper.isBlank(email))
            {
                throwsException("Username cannot be blank")
            }
            if (CommonHelper.isBlank(password))
            {
                throwsException("Password cannot be blank")
            }
            val user = validate(email, password)
            credentials.userProfile = CommonProfile().apply {
                setId(email)
                this.user = user
            }
        }
    }

    private fun validate(email: String, password: String): User
    {
        return JooqContext().use { db ->
            val repo = JooqUserRepository(db.dsl)
            val user = repo.getUserByEmail(email)
            if (user == null)
            {
                throw CredentialsException("Unknown email '$email'")
            }
            else
            {
                if (user.passwordHash == null)
                {
                    throwsException("User does not have a password")
                }
                if (!passwordEncoder.matches(password, user.passwordHash))
                {
                    throwsException("Provided password does not match password on record")
                }
                user
            }
        }
    }

    private fun throwsException(message: String)
    {
        throw CredentialsException(message)
    }
}
