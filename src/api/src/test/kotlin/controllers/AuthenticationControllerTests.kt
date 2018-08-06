package controllers

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.FormHelpers
import org.mrc.ide.auth.api.HTMLForm
import org.mrc.ide.auth.api.controllers.AuthenticationController
import org.mrc.ide.auth.api.security.user
import org.mrc.ide.auth.db.UserRepository
import org.mrc.ide.auth.models.User
import org.mrc.ide.auth.models.UserProperties
import org.mrc.ide.auth.security.WebTokenHelper
import org.pac4j.core.profile.CommonProfile
import java.time.Duration

class AuthenticationControllerTests
{
    private val fakeUser = User(UserProperties("testusername", "", "", "", null),
        listOf(),
        listOf())

    @Test
    fun `successful authentication updates last logged in timestamp`()
    {
        val fakeUserRepo = mock<UserRepository>()

        val fakeProfile = CommonProfile()
        fakeProfile.user = fakeUser

        val fakeContext = mock<ActionContext> {
            on { it.userProfile } doReturn fakeProfile
        }

        val fakeFormHelpers = mock<FormHelpers> {
            on {
                it.checkForm(fakeContext, mapOf("grant_type" to "client_credentials"))
            } doReturn (HTMLForm.ValidForm())
        }

        val fakeWebTokenHelper = mock<WebTokenHelper> {
            on { it.generateToken(eq(fakeUser), any()) } doReturn "token"
            on { it.defaultLifespan } doReturn Duration.ofHours(1)
        }

        val sut = AuthenticationController(fakeContext, fakeUserRepo,
                fakeFormHelpers, fakeWebTokenHelper)

        sut.authenticate()
        verify(fakeUserRepo).updateLastLoggedIn("testusername")
    }

    @Test
    fun `unsuccessful authentication does not update last logged in timestamp`()
    {
        val fakeUserRepo = mock<UserRepository>()

        val fakeContext = mock<ActionContext>()
        val fakeFormHelpers = mock<FormHelpers>() {
            on {
                it.checkForm(fakeContext, mapOf("grant_type" to "client_credentials"))
            } doReturn (HTMLForm.InvalidForm(""))
        }

        val fakeWebTokenHelper = mock<WebTokenHelper>()

        val sut = AuthenticationController(fakeContext, fakeUserRepo,
                fakeFormHelpers, fakeWebTokenHelper)

        sut.authenticate()
        verify(fakeUserRepo, never()).updateLastLoggedIn(any())
    }

}
