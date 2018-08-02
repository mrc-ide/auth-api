package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.models.permissions.PermissionSet
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.pac4j.core.profile.CommonProfile

private const val USER_OBJECT = "userObject"
private const val MISSING_PERMISSIONS = "missingPermissions"
private const val PERMISSIONS = "userPermissions"
private const val MISMATCHED_URL = "mismatchedURL"

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

val CommonProfile.missingPermissions: MutableSet<ReifiedPermission>
    get() = this.getAttributeOrDefault(MISSING_PERMISSIONS, default = mutableSetOf())

var CommonProfile.userPermissions: PermissionSet
    get() = this.getAttributeOrDefault(PERMISSIONS, PermissionSet())
    set(value) = this.addAttribute(PERMISSIONS, value)

private fun <T> CommonProfile.getAttributeOrDefault(key: String, default: T): T
{
    if (this.attributes.containsKey(key))
    {
        @Suppress("UNCHECKED_CAST")
        return this.attributes[key] as T
    }
    else
    {
        this.addAttribute(key, default)
        return default
    }
}