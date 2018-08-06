package org.mrc.ide.auth.emails

interface EmailData
{
    val subject: String
    fun text(): String
    fun html(): String
}