package controllers

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.controllers.PasswordController
import org.mrc.ide.auth.api.errors.MissingRequiredParameterError
import org.mrc.ide.auth.db.TokenRepository
import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.emails.EmailManager
import org.mrc.ide.auth.emails.PasswordSetEmail
import org.mrc.ide.auth.models.SetPassword
import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.models.UserProperties
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.auth.security.deflated
import java.time.Duration

class PasswordControllerTests {

    private val tokenHelper = mock<WebTokenHelper> {
        on {
            generateSetPasswordToken(any(), any())
        } doReturn "TOKEN"
    }

    @Test
    fun `can set password`() {
        val userRepo = mock<UserRepository>()
        val tokenRepo = mock<TokenRepository>()
        val tokenHelper = mock<WebTokenHelper>()
        val model = SetPassword("new_password")
        val context = mock<ActionContext> {
            on { postData(SetPassword::class.java) } doReturn model
            on { username } doReturn "thisUser"
        }
        val sut = PasswordController(context, userRepo, tokenRepo, tokenHelper, mock())
        val response = sut.setPassword()
        verify(userRepo).setPassword("thisUser", "new_password")
        assertThat(response).isEqualTo("OK")
    }

    @Test
    fun `can request set password email`() {
        val emailManager = mock<EmailManager>()
        val tokenHelper = mock<WebTokenHelper> {
            on {
                generateSetPasswordToken(any(), any())
            } doReturn "TOKEN"
        }
        val sut = PasswordController(context, userRepo, mock(), tokenHelper, emailManager)

        assertThat(sut.requestResetPasswordLink()).isEqualTo("OK")
        verify(emailManager).sendEmail(check {
            if (it is PasswordSetEmail) {
                assertThat(it.compressedToken).isEqualTo("TOKEN".deflated())
                assertThat(it.recipientName).isEqualTo("name")
            } else {
                fail("Expected '$it' to be an instance of PasswordSetEmail")
            }
        }, argThat { this == user })
    }

    @Test
    fun `can get set password onetime token`() {

        val sut = PasswordController(context, userRepo, mock(), tokenHelper, mock())

        sut.requestResetPasswordLink()

        verify(tokenHelper).generateSetPasswordToken(
                eq(Duration.ofDays(1)),
                eq(user.username)
        )
    }


    @Test
    fun `requesting set password email for unknown email fails silently`() {
        val emailManager = mock<EmailManager>()
        val context = mock<ActionContext> {
            on { queryParams("email") } doReturn "unknown@example.com"
        }
        val sut = PasswordController(context, mock(), mock(),tokenHelper, mock())
        assertThat(sut.requestResetPasswordLink()).isEqualTo("OK")
        verify(emailManager, never()).sendEmail(any(), any())
    }

    @Test
    fun `requesting set password email without email fails with error`() {
        val emailManager = mock<EmailManager>()
        val context = mock<ActionContext>()
        val sut = PasswordController(context, mock(), mock(), mock(), mock())

        assertThatThrownBy { sut.requestResetPasswordLink() }
                .isInstanceOf(MissingRequiredParameterError::class.java)
        verify(emailManager, never()).sendEmail(any(), any())
    }

    private val user = User(
            UserProperties("fake.user", "name", "fake@example.com", null, null),
            roles = emptyList(),
            permissions = emptyList()
    )

    private val context = mock<ActionContext> {
        on { queryParams("email") } doReturn "fake@example.com"
    }

    private val userRepo = mock<UserRepository> {
        on { getUserByEmail("fake@example.com") } doReturn user
    }

}