import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import org.junit.Test
import org.mrc.ide.auth.api.addTrailingSlashes

class HelpersTests
{
    @Test
    fun `addTrailingSlashes does not add slash if one already exists`()
    {
        val res = mock<spark.Response>()
        addTrailingSlashes(mockedRequest("http://example.com/"), res)
        verifyZeroInteractions(res)
    }

    @Test
    fun `addTrailingSlashes does add slash if one is missing`()
    {
        val res = mock<spark.Response>()
        addTrailingSlashes(mockedRequest("http://example.com"), res)
        verify(res).redirect("http://example.com/")
    }

    @Test
    fun `addTrailingSlashes does not tamper with query string`()
    {
        val req = mock<spark.Request> {
            on { pathInfo() } doReturn "http://example.com"
            on { queryString() } doReturn "p=1"
        }
        val res = mock<spark.Response>()
        addTrailingSlashes(req, res)
        verify(res).redirect("http://example.com/?p=1")
    }

    fun mockedRequest(url: String) = mock<spark.Request> {
        on { pathInfo() } doReturn url
    }
}