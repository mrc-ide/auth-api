package org.mrc.ide.auth.api

import org.pac4j.core.profile.CommonProfile
import spark.Request
import java.io.OutputStream
import java.io.Reader

interface ActionContext
{
    val request: Request
    val userProfile: CommonProfile?
    /** If the user logged in with a token this will be their username
     *  Otherwise it is null
     */
    val username: String?
    fun queryParams(key: String): String?
    fun contentType(): String
    fun params(key: String): String
    fun <T : Any> postData(klass: Class<T>): T
}


inline fun <reified T : Any> ActionContext.postData() = this.postData(T::class.java)