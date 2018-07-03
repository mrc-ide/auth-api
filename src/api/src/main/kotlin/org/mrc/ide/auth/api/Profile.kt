package org.mrc.ide.auth.api

import org.mrc.ide.auth.models.User
import org.pac4j.core.profile.CommonProfile

private const val USER_OBJECT = "userObject"

var CommonProfile.user: User?
    get()
    {
        val user = this.getAttribute(USER_OBJECT)
        return if (user != null && user is User)
        {
            user
        }
        else
        {
            null
        }
    }
    set(value) = this.addAttribute(USER_OBJECT, value)