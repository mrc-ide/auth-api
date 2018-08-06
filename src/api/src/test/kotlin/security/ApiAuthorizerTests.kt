package security

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions
import org.junit.Test
import org.mrc.ide.auth.api.security.ApiAuthorizer
import org.pac4j.core.profile.CommonProfile
import org.pac4j.sparkjava.SparkWebContext

class ApiAuthorizerTests
{
    @Test
    fun `is not authorized if url claim does not match request`()
    {
        val profile = CommonProfile().apply {
            addAttribute("url", "some/url")
        }
        val fakeContext = mock<SparkWebContext> {
            on(it.path) doReturn "/fake/url/"
        }

        val sut = ApiAuthorizer(setOf())
        val result = sut.isAuthorized(fakeContext, listOf(profile))
        Assertions.assertThat(result).isFalse()

    }

    @Test
    fun `is authorized if claim is global *`()
    {
        val profile = CommonProfile().apply {
            addAttribute("url", "*")
        }
        val fakeContext = mock<SparkWebContext> {
            on(it.path) doReturn "/fake/url/"
        }

        val sut = ApiAuthorizer(setOf())
        val result = sut.isAuthorized(fakeContext, listOf(profile))
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun `is authorized if url claim matches request and query params match`()
    {
        val sut = ApiAuthorizer(setOf())

        val profile = CommonProfile()
        profile.addAttribute("url", "/some/url/?query=whatever")

        val fakeContext = mock<SparkWebContext>() {
            on(it.path) doReturn "/some/url/?query=whatever"
        }

        val result = sut.isAuthorized(fakeContext, listOf(profile))

        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun `is not authorized if request does not contain same query params as claim`()
    {
        val sut = ApiAuthorizer(setOf())

        val profile = CommonProfile()
        profile.addAttribute("url", "/some/url/?query=whatever")

        val fakeContext = mock<SparkWebContext>() {
            on(it.path) doReturn "/some/url/"
        }

        val result = sut.isAuthorized(fakeContext, listOf(profile))

        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `is not authorized if claim does not have same query params as request`()
    {
        val sut = ApiAuthorizer(setOf())

        val profile = CommonProfile()
        profile.addAttribute("url", "/some/url/")

        val fakeContext = mock<SparkWebContext>() {
            on(it.path) doReturn "/some/url/?query=whatever"
        }

        val result = sut.isAuthorized(fakeContext, listOf(profile))

        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `is sensitive to query parameter values`()
    {
        val sut = ApiAuthorizer(setOf())

        val profile = CommonProfile()
        profile.addAttribute("url", "/some/url/?query=whatever")

        val fakeContext = mock<SparkWebContext>() {
            on(it.path) doReturn "/some/url/?query=somethingelse"
        }

        val result = sut.isAuthorized(fakeContext, listOf(profile))

        Assertions.assertThat(result).isFalse()
    }
}