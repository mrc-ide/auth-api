package org.mrc.ide.auth.db

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.mrc.ide.auth.db.Tables.*
import java.sql.Timestamp
import java.time.Instant
import org.mrc.ide.auth.models.*
import org.mrc.ide.auth.models.permissions.*
import org.mrc.ide.auth.security.SodiumPasswordEncoder
import org.pac4j.core.credentials.password.PasswordEncoder

interface UserRepository: Repository {
    fun updateLastLoggedIn(username: String)
    fun getUserByEmail(email: String): User?
    fun setPassword(username: String, plainPassword: String)
}

class JooqUserRepository(val dsl: DSLContext, private val passwordEncoder: PasswordEncoder): UserRepository {

    constructor(): this(JooqContext().dsl, SodiumPasswordEncoder())

    override fun setPassword(username: String, plainPassword: String) {
        val hashedPassword = passwordEncoder.encode(plainPassword)
        dsl.update(APP_USER).set(APP_USER.PASSWORD_HASH, hashedPassword)
                .where(APP_USER.USERNAME.eq(username))
                .execute()
    }

    override fun updateLastLoggedIn(username: String) {
        dsl.update(APP_USER)
                .set(APP_USER.LAST_LOGGED_IN, Timestamp.from(Instant.now()))
                .where(APP_USER.USERNAME.eq(username))
                .execute()
    }

    override fun getUserByEmail(email: String): User? {
        val user = dsl.fetchAny(APP_USER, caseInsensitiveEmailMatch(email))
        return if (user != null) {
            val records = getRolesAndPermissions(user.username)
            return User(
                    user.into(UserProperties::class.java),
                    records.map(this::mapRole).distinct(),
                    records.filter { it[PERMISSION.NAME] != null }.map(this::mapPermission))

        } else {
            null
        }
    }

    private fun mapPermission(record: Record) = ReifiedPermission(record[PERMISSION.NAME], mapScope(record))

    private fun mapRole(record: Record) = ReifiedRole(record[ROLE.NAME], mapScope(record))

    private fun mapScope(record: Record): Scope {
        val scopePrefix = record[ROLE.SCOPE_PREFIX]
        val scopeId = record[USER_GROUP_ROLE.SCOPE_ID]
        if (scopePrefix != null) {
            return Scope.Specific(scopePrefix, scopeId)
        } else {
            return Scope.Global()
        }
    }

    private fun caseInsensitiveEmailMatch(email: String) = APP_USER.EMAIL.lower().eq(email.toLowerCase())

    private fun caseInsensitiveUsernameMatch(username: String) = APP_USER.USERNAME.lower().eq(username.toLowerCase())

    private fun getRolesAndPermissions(username: String): Result<Record> {
        return dsl.select(PERMISSION.NAME)
                .select(ROLE.NAME, ROLE.SCOPE_PREFIX)
                .select(USER_GROUP_ROLE.SCOPE_ID)
                .fromJoinPath(APP_USER,
                        USER_GROUP_MEMBERSHIP,
                        USER_GROUP,
                        USER_GROUP_ROLE,
                        ROLE)
                .leftJoin(ROLE_PERMISSION)
                .on(ROLE_PERMISSION.ROLE.eq(ROLE.ID))
                .leftJoin(PERMISSION)
                .on(ROLE_PERMISSION.PERMISSION.eq(PERMISSION.NAME))
                .where(caseInsensitiveUsernameMatch(username))
                .fetch()

    }
}