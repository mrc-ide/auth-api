package errors

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.api.errors.ApiError
import org.mrc.ide.serialization.models.ErrorInfo

class MontaguErrorTests
{
    @Test
    fun errorsAreFormattedCorrectly()
    {
        val errors = listOf(
                ErrorInfo("code1", "message1"),
                ErrorInfo("code2", "message2")
        )
        val actual = ApiError.formatProblemsIntoMessage(errors)
        val expected = """the following problems occurred:
message1
message2"""
        assertThat(actual).isEqualTo(expected)
    }
}