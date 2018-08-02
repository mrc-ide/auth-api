package org.mrc.ide.auth.api.errors

import org.mrc.ide.serialization.models.ErrorInfo
import org.mrc.ide.serialization.models.Result
import org.mrc.ide.serialization.models.ResultStatus


abstract class ApiError(
        open val httpStatus: Int,
        val problems: Iterable<ErrorInfo>
) : Exception(formatProblemsIntoMessage(problems))
{
    open fun asResult() = Result(ResultStatus.FAILURE, null, problems)

    companion object
    {
        fun formatProblemsIntoMessage(problems: Iterable<ErrorInfo>): String
        {
            val joined = problems.map { it.message }.joinToString("\n")
            return "the following problems occurred:\n$joined"
        }
    }
}