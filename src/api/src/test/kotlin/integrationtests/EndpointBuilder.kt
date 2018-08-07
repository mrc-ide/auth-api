package integrationtests

import org.mrc.ide.auth.api.AppConfig


object EndpointBuilder
{
    val hostUrl = AppConfig.url
    val baseUrl = "v1"
    val root = "$hostUrl/$baseUrl"

    fun build(url: String) = root + url
    fun buildPath(url: String) = "/$baseUrl" + url
}