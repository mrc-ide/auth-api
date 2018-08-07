package integrationtests

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import khttp.extensions.fileLike
import khttp.responses.Response
import khttp.structures.files.FileLike
import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.serialization.models.ContentTypes
import org.mrc.ide.serialization.models.ErrorInfo
import java.io.File

data class TokenLiteral(val value: String)
{
    override fun toString() = value
}

class RequestHelper
{
    init
    {
        CertificateHelper.disableCertificateValidation()
    }

    fun get(url: String, permissions: Set<ReifiedPermission>, acceptsContentType: String = ContentTypes.json): Response
    {
        val token = TestUserHelper().getTokenForTestUser(permissions)
        return get(url, token, acceptsContentType = acceptsContentType)
    }

    fun get(url: String, token: TokenLiteral? = null, acceptsContentType: String = ContentTypes.json): Response
    {
        return get(url, standardHeaders(acceptsContentType, token))
    }

    fun post(url: String, permissions: Set<ReifiedPermission>, data: JsonObject): Response
    {
        return post(url, permissions, data.toJsonString(prettyPrint = true))
    }
    fun post(url: String, permissions: Set<ReifiedPermission>, data: String? = null): Response
    {
        val token = TestUserHelper().getTokenForTestUser(permissions)
        return post(url, data, token = token)
    }

    fun post(url: String, data: JsonObject,
             token: TokenLiteral? = null,
             acceptsContentType: String = ContentTypes.json
    ): Response
    {
        return post(url, data.toJsonString(prettyPrint = true),
                token = token,
                acceptsContentType = acceptsContentType)
    }
    fun post(url: String, data: String? = null,
             token: TokenLiteral? = null,
             acceptsContentType: String = ContentTypes.json
    ): Response
    {
        return post(
                url,
                standardHeaders(acceptsContentType, token),
                data
        )
    }

    fun standardHeaders(acceptsContentType: String, token: TokenLiteral?): Map<String, String> {
        var headers = mapOf(
                "Accept" to acceptsContentType,
                "Accept-Encoding" to "gzip"
        )
        if (token != null) {
            headers += mapOf("Authorization" to "Bearer $token")
        }
        return headers
    }

    private fun post(url: String, headers: Map<String, String>, data: Any?) = khttp.post(
            EndpointBuilder.build(url),
            data = data,
            headers = headers,
            allowRedirects = false
    )

    private fun postFiles(url: String, headers: Map<String, String>, files: List<FileLike>, data: Map<String, String> = mapOf()) = khttp.post(
            EndpointBuilder.build(url),
            headers = headers,
            files = files,
            data = data,
            allowRedirects = false
    )

    private fun get(url: String, headers: Map<String, String>)
            = khttp.get(EndpointBuilder.build(url), headers)
}

fun <T> Response.data(): T?
{
    val data = this.json()["data"]
    if (data != "")
    {
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
    else
    {
        return null
    }
}

fun Response.errors(): List<ErrorInfo>
{
    val errors = json()["errors"]
    if (errors is JsonArray<*>)
    {
        return errors.filterIsInstance<JsonObject>().map {
            ErrorInfo(it["code"] as String, it["message"] as String)
        }
    }
    else
    {
        throw Exception("Unable to get error collection from this response: " + this.text)
    }
}

fun Response.json() = try
{
    Parser().parse(StringBuilder(text)) as JsonObject
}
catch (e: RuntimeException)
{
    throw Exception("Unable to parse response as JSON: $text")
}