package org.mrc.ide.auth.models

data class ErrorInfo(val code: String, val message: String)
{
    override fun toString(): String = message
}
