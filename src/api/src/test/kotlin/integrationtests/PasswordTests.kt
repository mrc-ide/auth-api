package integrationtests

import com.beust.klaxon.json
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.mrc.ide.auth.emails.WriteToDiskEmailManager

class PasswordTests : IntegrationTest()
{
    @Test
    fun `can set password`()
    {
        val token = TestUserHelper.setupTestUserAndGetToken(setOf(), true)
        val response = requestHelper.post("/password/set/", json {
            obj("password" to "new_password")
        }, token)

        val data = response.data<String>()
        assertThat(data).isEqualTo("OK")

        checkPasswordHasChangedForTestUser("new_password")
    }

    @Test
    fun `can request set password link`()
    {
        TestUserHelper.setupTestUser()
        WriteToDiskEmailManager.cleanOutputDirectory()
        val requestHelper = RequestHelper()

        // Request the link via (fake) email
        val url = "/password/request-link/?email=${TestUserHelper.email}"
        val response = requestHelper.post(url, null)
        val data = response.data<String>()
        assertThat(data).isEqualTo("OK")

        // Use the token to change the password
        val token = getTokenFromFakeEmail()
        requestHelper.post("/password/set/?access_token=$token/", json {
            obj("password" to "new_password")
        })

        checkPasswordHasChangedForTestUser("new_password")
    }

    companion object
    {
        fun getTokenFromFakeEmail(): String
        {
            val emailFile = WriteToDiskEmailManager.outputDirectory.listFiles().singleOrNull()
                    ?: throw Exception("No emails were found in ${WriteToDiskEmailManager.outputDirectory}")
            val text = emailFile.readText()
            val match = Regex("""token=([^\n]+)\n""").find(text)
            return match?.groups?.get(1)?.value
                    ?: throw Exception("Unable to find token in $text")
        }

        fun checkPasswordHasChangedForTestUser(password: String)
        {
            assertThatThrownBy { TestUserHelper().getTokenForTestUser() }
            assertThat(TestUserHelper(password).getTokenForTestUser())
                    .isInstanceOf(TokenLiteral::class.java)
        }
    }
}