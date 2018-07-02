package org.mrc.ide.auth.security

import org.jooq.DSLContext
import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.Tables.*
import org.mrc.ide.auth.db.fieldsAsList
import org.mrc.ide.auth.models.permissions.ReifiedRole

fun JooqContext.givePermissionsToUserUsingTestRole(
        username: String,
        scopePrefix: String?,
        scopeId: String,
        permissions: List<String>)
{
    val testRoleId = this.getOrCreateRole("test_role_$scopeId", scopePrefix, "Test role")
    dsl.ensureUserHasRole(username, testRoleId, scopeId = scopeId)
    this.setRolePermissions(testRoleId, permissions)
}

fun JooqContext.clearRolesForUser(username: String)
{
    val groups = dsl.select(USER_GROUP.ID)
            .from(USER_GROUP).join(USER_GROUP_MEMBERSHIP)
            .on(USER_GROUP.ID.eq(USER_GROUP_MEMBERSHIP.USER_GROUP))
            .where(USER_GROUP_MEMBERSHIP.USERNAME.eq(username))

    dsl.deleteFrom(USER_GROUP_ROLE)
            .where(USER_GROUP_ROLE.USER_GROUP.`in`(groups)).execute()
}

fun JooqContext.setRolePermissions(roleId: Int, permissions: List<String>)
{
    dsl.deleteFrom(ROLE_PERMISSION).where(ROLE_PERMISSION.ROLE.eq(roleId)).execute()
    val records = permissions.map { permission ->
        dsl.newRecord(ROLE_PERMISSION).apply {
            this.role = roleId
            this.permission = permission
        }
    }
    dsl.batchStore(records).execute()
}

fun JooqContext.getOrCreateRole(name: String, scopePrefix: String?, description: String): Int
{
    val id = getRole(name, scopePrefix)
    if (id != null)
    {
        return id
    }
    else
    {
        return createRole(name, scopePrefix, description)
    }
}

fun JooqContext.getRole(name: String, scopePrefix: String?): Int?
{
    return dsl.getRole(name, scopePrefix)
}

fun DSLContext.getRole(name: String, scopePrefix: String?): Int?
{
    val role = this.select(ROLE.ID)
            .from(ROLE)
            .where(ROLE.NAME.eq(name))
            // Dealing with SQLs ternary NULL logic - this is just an equality check
            .and(ROLE.SCOPE_PREFIX.isNotDistinctFrom(scopePrefix))
            .fetchAny()

    return role?.get(ROLE.ID)
}

fun JooqContext.createRole(name: String, scopePrefix: String?, description: String): Int
{
    val role = dsl.newRecord(ROLE).apply {
        this.name = name
        this.scopePrefix = scopePrefix
        this.description = description
    }
    role.store()
    return role.id
}

fun JooqContext.ensureUserHasRole(username: String, roleId: Int, scopeId: String)
{
    dsl.ensureUserHasRole(username, roleId, scopeId)
}

fun DSLContext.ensureUserHasRole(username: String, roleId: Int, scopeId: String)
{
    val groupRoleMapping = this.select(USER_GROUP_ROLE.fieldsAsList())
            .from(USER_GROUP_ROLE)
            .where(USER_GROUP_ROLE.USER_GROUP.eq(username))
            .and(USER_GROUP_ROLE.ROLE.eq(roleId))
            .and(USER_GROUP_ROLE.SCOPE_ID.eq(scopeId))
            .fetchAny()
    if (groupRoleMapping == null)
    {
        this.newRecord(USER_GROUP_ROLE).apply {
            this.userGroup = username
            this.role = roleId
            this.scopeId = scopeId
        }.store()
    }
}

fun JooqContext.ensureUserHasRole(username: String, role: ReifiedRole)
{
    this.dsl.ensureUserHasRole(username, role)
}

fun DSLContext.ensureUserHasRole(username: String, role: ReifiedRole)
{
    val roleId = this.getRole(role.name, role.scope.databaseScopePrefix)
            ?: throw UnknownRoleException(role.name, role.scope.databaseScopePrefix.toString())
    this.ensureUserHasRole(username, roleId, role.scope.databaseScopeId)
}