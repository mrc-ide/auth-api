package security

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.models.UserProperties
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.auth.models.permissions.ReifiedRole
import org.mrc.ide.auth.models.permissions.Scope
import org.mrc.ide.auth.security.*
import java.time.Duration
import java.time.Instant
import java.util.*

class WebTokenHelperTests
{
    lateinit var sut: WebTokenHelper
    val properties = UserProperties(
            username = "test.user",
            name = "Test User",
            email = "test@example.com",
            passwordHash = "",
            lastLoggedIn = null
    )
    val roles = listOf(
            ReifiedRole("roleA", Scope.Global()),
            ReifiedRole("roleB", Scope.Specific("prefix", "id"))
    )
    val permissions = listOf(
            ReifiedPermission("p1", Scope.Global()),
            ReifiedPermission("p2", Scope.Specific("prefix", "id"))
    )

    @Before
    fun setUp()
    {
        createHelper()
    }

    private fun createHelper()
    {
        sut = WebTokenHelper("fakeissuer", 3600, KeyHelper("").keyPair)
    }

    @Test
    fun `can generate bearer token`()
    {
        val token = sut.generateToken(User(properties, roles, permissions))

        val claims = CompressedTokenAuthenticator(sut).validateTokenAndGetClaims(token.deflated())

        assertThat(claims["iss"]).isEqualTo("fakeissuer")
        assertThat(claims["token_type"]).isEqualTo("BEARER")
        assertThat(claims["sub"]).isEqualTo("test.user")
        assertThat(claims["exp"]).isInstanceOf(Date::class.java)
        assertThat(claims["roles"]).isEqualTo("*/roleA,prefix:id/roleB")
        assertThat(claims["permissions"]).isEqualTo("*/p1,prefix:id/p2")
    }

    @Test
    fun `can generate long-lived bearer token`()
    {
        val token = sut.generateToken(User(properties, roles, permissions), lifeSpan = Duration.ofDays(365))
        val claims = CompressedTokenAuthenticator(sut).validateTokenAndGetClaims(token.deflated())

        assertThat(claims["exp"] as Date).isAfter(Date.from(Instant.now() + Duration.ofDays(364)))
    }

    @Test
    fun `token fails validation when issuer is wrong`()
    {
        val claims = sut.claims(User(properties, roles, permissions))
        val badToken = sut.generator.generate(claims.plus("iss" to "unexpected.issuer"))
        val verifier = CompressedTokenAuthenticator(sut)
        assertThat(verifier.validateToken(badToken)).isNull()
    }

    @Test
    fun `token fails validation when token type is wrong`()
    {
        val claims = sut.claims(User(properties, roles, permissions))
        val badToken = sut.generator.generate(claims.plus("token_type" to "unexpected.type"))
        val verifier = CompressedTokenAuthenticator(sut)
        assertThat(verifier.validateToken(badToken)).isNull()
    }

    @Test
    fun `token fails validation when token is old`()
    {
        val claims = sut.claims(User(properties, roles, permissions))
        val badToken = sut.generator.generate(claims.plus("exp" to Date.from(Instant.now())))
        val verifier = CompressedTokenAuthenticator(sut)
        assertThat(verifier.validateToken(badToken)).isNull()
    }

    @Test
    fun `token fails validation when token is signed by wrong key`()
    {
        val sauron = WebTokenHelper("fakeissuer", 3600, KeyHelper.generateKeyPair())
        val evilToken = sauron.generateToken(User(properties, roles, permissions))
        val verifier = CompressedTokenAuthenticator(sut)
        assertThat(verifier.validateToken(evilToken)).isNull()
    }

    @Test
    fun `can generate set password token`()
    {
        val mockTokenChecker = mock<OneTimeTokenChecker> {
            on { checkToken(any()) } doReturn true
        }
        val token = sut.generateSetPasswordToken(Duration.ofDays(1), "username")
        val claims = OneTimeTokenAuthenticator(sut, mockTokenChecker)
                .validateTokenAndGetClaims(token.deflated())

        assertThat(claims["iss"]).isEqualTo("fakeissuer")
        assertThat(claims["token_type"]).isEqualTo("ONETIME")
        assertThat(claims["sub"]).isEqualTo("SET_PASSWORD")
        assertThat(claims["exp"] as Date).isAfter(Date.from(Instant.now()))
        assertThat(claims["nonce"]).isNotNull()
    }

    @Test
    fun `set password token fails validation when token fails onetime check`()
    {
        val mockTokenChecker = mock<OneTimeTokenChecker> {
            on { checkToken(any()) } doReturn false
        }
        val token = sut.generateSetPasswordToken(Duration.ofDays(1), "username")
        assertThatThrownBy { OneTimeTokenAuthenticator(sut, mockTokenChecker)
                .validateTokenAndGetClaims(token.deflated()) }
    }

}