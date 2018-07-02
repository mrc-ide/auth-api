package org.mrc.ide.auth.security

import org.jooq.DSLContext
import org.pac4j.core.credentials.password.PasswordEncoder
import org.mrc.ide.auth.db.Tables
import org.mrc.ide.auth.db.Tables.APP_USER

object UserHelper
{
    val encoder: PasswordEncoder = SodiumPasswordEncoder()

    fun saveUser(db: DSLContext, username: String, name: String, email: String, plainPassword: String)
    {
        db.newRecord(APP_USER).apply {
            this.username = username
            this.name = name
            this.email = email
            this.passwordHash = hashedPassword(plainPassword)
        }.store()

        db.newRecord(Tables.USER_GROUP).apply {
            this.name = username
            id = username
        }.insert()

        db.newRecord(Tables.USER_GROUP_MEMBERSHIP).apply {
            this.username = username
            userGroup = username
        }.insert()
    }

    fun userExists(db: DSLContext, username: String): Boolean
    {
        return db.fetchOne(APP_USER, APP_USER.USERNAME.eq(username)) != null
    }

    fun hashedPassword(plainPassword: String) = encoder.encode(plainPassword)

    fun suggestUsername(name: String): String
    {
        val names = name.toLowerCase().split(' ')
        var username = names.first()
        if (names.size > 1)
        {
            username = "$username.${names.last()}"
        }
        return username
    }
}