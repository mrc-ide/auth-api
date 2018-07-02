package org.mrc.ide.auth.models

import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.auth.models.permissions.ReifiedRole
import java.beans.ConstructorProperties
import java.time.Instant

data class User(
        val properties: UserProperties,
        val roles: List<ReifiedRole>,
        val permissions: List<ReifiedPermission>
) : UserPropertiesInterface by properties

interface BasicUserProperties
{
    val username: String
    val name: String
    val email: String
}

interface UserPropertiesInterface : BasicUserProperties
{
    val passwordHash: String?
    val lastLoggedIn: Instant?
}

data class UserProperties
@ConstructorProperties("username", "name", "email", "passwordHash", "lastLoggedIn")
constructor(
        override val username: String,
        override val name: String,
        override val email: String,
        override val passwordHash: String?,
        override val lastLoggedIn: Instant?
) : UserPropertiesInterface