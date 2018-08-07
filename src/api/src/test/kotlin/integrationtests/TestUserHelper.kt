package integrationtests

import org.mrc.ide.auth.db.*
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.auth.models.permissions.Scope

class TestUserHelper(private val password: String = TestUserHelper.defaultPassword)
{
    private val tokenFetcher = TokenFetcher()

    fun setupTestUser(db: JooqContext)
    {
        if (!db.userExists(username))
        {
            db.addUserForTesting(username, "Test User", email, password)
        }
    }

    fun getTokenForTestUser(
            permissions: Set<ReifiedPermission> = emptySet(),
            includeCanLogin: Boolean = false
    ): TokenLiteral
    {
        val extraPermissions = if (includeCanLogin)
        {
            setOf(ReifiedPermission("can-login", Scope.Global()))
        }
        else
        {
            emptySet()
        }

        JooqContext().use {
            it.clearRolesForUser(username)
            for ((scope, subset) in (permissions + extraPermissions).groupBy { it.scope })
            {
                val names = subset.map { it.name }
                it.givePermissionsToUserUsingTestRole(
                        username,
                        scope.databaseScopePrefix,
                        scope.databaseScopeId,
                        names
                )
            }
        }
        val token = tokenFetcher.getToken(email, password)
        return when (token)
        {
            is TokenFetcher.TokenResponse.Token -> token.token
            is TokenFetcher.TokenResponse.Error -> throw Exception("Unable to obtain auth token: '${token.message}'")
        }
    }

    companion object
    {
        val username = "test.user"
        val email = "user@test.com"
        val defaultPassword = "test"

        fun setupTestUser()
        {
            JooqContext().use {
                TestUserHelper().setupTestUser(it)
            }
        }

        fun setupTestUserAndGetToken(
                permissions: Set<ReifiedPermission>,
                includeCanLogin: Boolean = false
        ): TokenLiteral
        {
            setupTestUser()
            return TestUserHelper().getTokenForTestUser(permissions, includeCanLogin)
        }
    }
}