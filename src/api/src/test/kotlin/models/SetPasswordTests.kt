package models

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.models.SetPassword
import org.mrc.ide.serialization.DefaultSerializer
import org.mrc.ide.serialization.ModelBinder

class SetPasswordTests
{
    private val model = SetPassword("password")
    private val binder = ModelBinder(DefaultSerializer.instance)

    @Test
    fun `valid model verifies`()
    {
        assertThat(binder.verify(model)).isEmpty()
    }

    @Test
    fun `password is required`()
    {
        assertCausesTheseErrors(model.copy(password = ""), "invalid-field:password:blank")
        assertCausesTheseErrors(model.copy(password = " \n "), "invalid-field:password:blank")
    }

    @Test
    fun `password must be at least 8 characters long`()
    {
        assertCausesTheseErrors(model.copy(password = "passwor"), "invalid-field:password:too-short")
    }

    protected fun <T : Any> assertCausesTheseErrors(badModel: T, vararg codes: String)
    {
        val actual = binder.verify(badModel)
        val actualCodes = actual.joinToString { it.code }
        Assertions.assertThat(actual.size)
                .`as`("Expected these errors: ${codes.joinToString()} for this model: $badModel.\nActual: $actualCodes")
                .isEqualTo(codes.size)
        for (code in codes)
        {
            val matching = actual.filter { it.code == code }
            Assertions.assertThat(matching)
                    .`as`("Expected this error '$code' for this model: $badModel. These errors were present: $actualCodes")
                    .isNotEmpty
        }
    }
}