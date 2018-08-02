package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.api.addDefaultResponseHeaders
import org.pac4j.sparkjava.DefaultHttpActionAdapter
import org.pac4j.sparkjava.SparkWebContext
import org.mrc.ide.serialization.DefaultSerializer
import org.mrc.ide.serialization.Serializer
import org.mrc.ide.serialization.models.ErrorInfo
import org.mrc.ide.serialization.models.Result
import org.mrc.ide.serialization.models.ResultStatus

abstract class HttpActionAdapter(private val serializer: Serializer = DefaultSerializer.instance)
    : DefaultHttpActionAdapter()
{
    protected fun haltWithError(code: Int, context: SparkWebContext, errors: List<ErrorInfo>)
    {
        haltWithError(code, context, serializer.toJson(Result(ResultStatus.FAILURE, null, errors)))
    }

    protected fun haltWithError(code: Int, context: SparkWebContext, response: String)
    {
        addDefaultResponseHeaders(context.sparkRequest, context.response)
        spark.Spark.halt(code, response)
    }
}