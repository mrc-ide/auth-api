import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.HTMLForm
import org.mrc.ide.auth.api.HTMLFormHelpers

class HTMLFormHelpersTests
{
    @Test
    fun `checkForm returns InvalidForm if content type does not match`()
    {
        val context = mock<ActionContext> {
            on { contentType() } doReturn "bad"
        }
        val result = HTMLFormHelpers().checkForm(context, emptyMap())
        assertThat(result).isInstanceOf(HTMLForm.InvalidForm::class.java)
    }

    @Test
    fun `checkForm returns InvalidForm if key is missing`()
    {
        val context = mock<ActionContext> {
            on { contentType() } doReturn "application/x-www-form-urlencoded"
            on { queryParams("key") } doReturn (null as String?)
        }
        val result = HTMLFormHelpers().checkForm(context, mapOf("key" to "value"))
        assertThat(result).isInstanceOf(HTMLForm.InvalidForm::class.java)
    }

    @Test
    fun `checkForm returns InvalidForm if key has wrong value`()
    {
        val context = mock<ActionContext> {
            on { contentType() } doReturn "application/x-www-form-urlencoded"
            on { queryParams("key") } doReturn "bad"
        }
        val result = HTMLFormHelpers().checkForm(context, mapOf("key" to "value"))
        assertThat(result).isInstanceOf(HTMLForm.InvalidForm::class.java)
    }

    @Test
    fun `checkForm returns ValidForm if all values match`()
    {
        val context = mock<ActionContext> {
            on { contentType() } doReturn "application/x-www-form-urlencoded"
            on { queryParams("key") } doReturn "value"
        }
        val result = HTMLFormHelpers().checkForm(context, mapOf("key" to "value"))
        assertThat(result).isEqualTo(HTMLForm.ValidForm())
    }
}