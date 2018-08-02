package org.mrc.ide.auth.api

import org.mrc.ide.serialization.models.ContentTypes
import spark.Filter
import spark.Request
import spark.Response
import spark.route.HttpMethod
import javax.servlet.http.HttpServletResponse


fun addDefaultResponseHeaders(req: Request, res: HttpServletResponse,
                              contentType: String = "${ContentTypes.json}; charset=utf-8")
{
    res.contentType = contentType
    val gzip = req.headers("Accept-Encoding")?.contains("gzip")
    if (gzip == true && res.getHeader("Content-Encoding") != "gzip")
    {
        res.addHeader("Content-Encoding", "gzip")
    }
}

fun addDefaultResponseHeaders(req: Request,
                              res: Response,
                              contentType: String = "${ContentTypes.json}; charset=utf-8") = addDefaultResponseHeaders(req, res.raw(), contentType)


class DefaultHeadersFilter(val contentType: String, val method: HttpMethod) : Filter
{
    override fun handle(request: Request, response: Response)
    {
        if (request.requestMethod().equals(method.toString(), ignoreCase = true))
        {
            addDefaultResponseHeaders(request, response, contentType)
        }
    }
}