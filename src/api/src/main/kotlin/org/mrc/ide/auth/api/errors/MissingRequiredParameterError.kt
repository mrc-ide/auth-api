package org.mrc.ide.auth.api.errors

import org.mrc.ide.serialization.models.ErrorInfo

class MissingRequiredParameterError(parameterName: String) : ApiError(400, listOf(ErrorInfo(
        "missing-required-parameter:$parameterName",
        "You must supply a '$parameterName' parameter in the query string"
)))
