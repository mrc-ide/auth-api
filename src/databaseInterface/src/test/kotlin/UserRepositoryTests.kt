import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.JooqUserRepository
import org.mrc.ide.auth.db.Tables
import org.mrc.ide.auth.db.Tables.*
import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.auth.models.permissions.ReifiedRole
import org.mrc.ide.auth.models.permissions.Scope
import org.mrc.ide.auth.security.SodiumPasswordEncoder
import java.time.Instant

class UserRepositoryTests : RepositoryTests<UserRepository>() {
    val username = "test.user"
    val email = "test@example.com"
    override fun makeRepository(db: JooqContext) = JooqUserRepository(db.dsl, SodiumPasswordEncoder())

    private fun addTestUser(db: JooqContext) {
        db.addUserForTesting(username, "Test User", email, "password")
    }

    @Test
    fun `can retrieve user with any case email address`() {
        given(this::addTestUser).check { repo ->
            checkUser(getUser(repo, email))
            checkUser(getUser(repo, email.toUpperCase()))
        }
    }

    @Test
    fun `cannot retrieve user with incorrect email address`() {
        given(this::addTestUser).check { repo ->
            assertThat(repo.getUserByEmail(username)).isNull()
            assertThat(repo.getUserByEmail("Test User")).isNull()
        }
    }

    @Test
    fun `can update last logged in`() {

        withDatabase(this::addTestUser)

        val then = Instant.now()

        withRepo { repo ->
            repo.updateLastLoggedIn(username)
            val user = repo.getUserByEmail(email)!!
            assertThat(user.lastLoggedIn).isNotNull()
            assertThat(user.lastLoggedIn).isBetween(then, Instant.now())
        }
    }

    @Test
    fun `can retrieve user by email with globally scoped permissions`() {
        given {
            addTestUser(it)
            val roleId = it.createRole("role", scopePrefix = null, description = "Role")
            createPermissions(it, listOf("p1", "p2"))
            it.setRolePermissions(roleId, listOf("p1", "p2"))
            it.ensureUserHasRole("test.user", roleId, scopeId = "")
        } check { repo ->
            val roles = listOf(ReifiedRole("role", Scope.Global()))
            val permissions = listOf(
                    ReifiedPermission("p1", Scope.Global()),
                    ReifiedPermission("p2", Scope.Global())
            )
            checkUser(getUser(repo, email), roles, permissions)
        }
    }

    @Test
    fun `can retrieve user by email with specifically scoped permissions`() {
        given {
            addTestUser(it)
            val roleGlobal = it.createRole("a", scopePrefix = null, description = "Role Global")
            val roleA = it.createRole("a", scopePrefix = "prefixA", description = "Role A")
            val roleB = it.createRole("b", scopePrefix = "prefixB", description = "Role B")
            createPermissions(it, listOf("p1", "p2", "p3"))
            it.setRolePermissions(roleGlobal, listOf("p1"))
            it.setRolePermissions(roleA, listOf("p1", "p2"))
            it.setRolePermissions(roleB, listOf("p1", "p3"))
            it.ensureUserHasRole("test.user", roleGlobal, scopeId = "")
            it.ensureUserHasRole("test.user", roleA, scopeId = "idA")
            it.ensureUserHasRole("test.user", roleB, scopeId = "idB")
        } check { repo ->
            val roles = listOf(
                    ReifiedRole("a", Scope.Global()),
                    ReifiedRole("a", Scope.Specific("prefixA", "idA")),
                    ReifiedRole("b", Scope.Specific("prefixB", "idB"))
            )
            val permissions = listOf(
                    ReifiedPermission("p1", Scope.Global()),
                    ReifiedPermission("p1", Scope.Specific("prefixA", "idA")),
                    ReifiedPermission("p2", Scope.Specific("prefixA", "idA")),
                    ReifiedPermission("p1", Scope.Specific("prefixB", "idB")),
                    ReifiedPermission("p3", Scope.Specific("prefixB", "idB"))
            )
            checkUser(getUser(repo, email), roles, permissions)
        }
    }

    @Test
    fun `can set password`() {
        given {
            it.addUserWithRoles("will")
        } makeTheseChanges {
            it.setPassword("will", "newpassword")
        } andCheckDatabase {
            val hash = it.dsl.select(APP_USER.PASSWORD_HASH)
                    .from(APP_USER)
                    .where(APP_USER.USERNAME.eq("will"))
                    .fetchOne().value1()
            assertThat(SodiumPasswordEncoder().matches("newpassword", hash)).isTrue()
        }
    }

    private fun checkUser(
            user: User,
            expectedRoles: List<ReifiedRole> = emptyList(),
            expectedPermissions: List<ReifiedPermission> = emptyList()) {
        assertThat(user.username).isEqualTo("test.user")
        assertThat(user.name).isEqualTo("Test User")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.roles).hasSameElementsAs(expectedRoles)
        assertThat(user.permissions).hasSameElementsAs(expectedPermissions)
    }

    private fun getUser(repository: UserRepository, email: String): User {
        val user = repository.getUserByEmail(email)
        assertThat(user).isNotNull()
        return user!!
    }

    private fun createPermissions(db: JooqContext, permissions: List<String>) {
        val records = permissions.map {
            db.dsl.newRecord(Tables.PERMISSION).apply { name = it }
        }
        db.dsl.batchStore(records).execute()
    }
}