package security

import org.assertj.core.api.Assertions
import org.junit.Test
import org.mrc.ide.auth.api.errors.PermissionRequirementParseException
import org.mrc.ide.auth.api.security.PermissionRequirement
import org.mrc.ide.auth.api.security.ScopeRequirement

class PermissionRequirementTests
{
    @Test
    fun `can parse globally scoped permission requirement`()
    {
        var permission = PermissionRequirement.parse("*/can-login")
        Assertions.assertThat(permission)
                .isEqualTo(PermissionRequirement("can-login", ScopeRequirement.Global()))
    }

    @Test
    fun `can parse specifically scoped permission requirement`()
    {
        var permission = PermissionRequirement.parse("prefix:<urlKey>/name")
        Assertions.assertThat(permission)
                .isEqualTo(PermissionRequirement("name", ScopeRequirement.Specific("prefix", "urlKey")))
    }

    @Test
    fun `badly formatted string throws parse exception`()
    {
        Assertions.assertThatThrownBy { PermissionRequirement.parse("") }
                .isInstanceOf(PermissionRequirementParseException::class.java)
    }

    @Test
    fun `badly formatted scope ID throws parse exception`()
    {
        Assertions.assertThatThrownBy { PermissionRequirement.parse("prefix:urlKey/name") }
                .isInstanceOf(PermissionRequirementParseException::class.java)
    }
}