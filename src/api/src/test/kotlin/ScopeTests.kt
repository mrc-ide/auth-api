import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.models.permissions.Scope.Global
import org.mrc.ide.auth.models.permissions.Scope.Specific
import org.mrc.ide.auth.models.permissions.Scope
import org.mrc.ide.auth.models.permissions.encompass

class ScopeTests
{
    @Test
    fun `global scope encompasses all scopes`()
    {
        val globalScope = Global()

        val specificScope = Specific("foo", "bar")

        assertThat(globalScope.encompasses(specificScope)).isTrue()
        assertThat(globalScope.encompasses(Global())).isTrue()
    }

    @Test
    fun `specific scope encompasses itself`()
    {
        val specificScope = Specific("foo", "bar")
        val specificScopeAgain = Specific("foo", "bar")

        assertThat(specificScope.encompasses(specificScopeAgain)).isTrue()
    }

    @Test
    fun `specific scope does not encompasses other scope`()
    {
        val specificScope = Specific("foo", "bar")
        val anotherSpecificScope = Specific("oof", "rab")

        assertThat(specificScope.encompasses(anotherSpecificScope)).isFalse()
        assertThat(specificScope.encompasses(Global())).isFalse()
    }

    @Test
    fun `empty list does not encompass any scope`()
    {
        val scopes = emptyList<Scope>()
        assertThat(scopes.encompass(Specific("a", "b"))).isFalse()
        assertThat(scopes.encompass(Global())).isFalse()
    }

    @Test
    fun `list with global in encompasses any scope`()
    {
        val scopes = listOf(Global())
        assertThat(scopes.encompass(Specific("a", "b"))).isTrue()
        assertThat(scopes.encompass(Global())).isTrue()
    }

    @Test
    fun `list with multiple specific scopes can encompass those scopes`()
    {
        val scopes = listOf(
                Specific("a", "1"),
                Specific("a", "2"),
                Specific("b", "1")
        )
        assertThat(scopes.encompass(Specific("a", "1"))).isTrue()
        assertThat(scopes.encompass(Specific("a", "2"))).isTrue()
        assertThat(scopes.encompass(Specific("b", "1"))).isTrue()
        assertThat(scopes.encompass(Specific("b", "2"))).isFalse()
    }
}