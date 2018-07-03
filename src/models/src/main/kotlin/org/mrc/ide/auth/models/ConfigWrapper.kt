package org.mrc.ide.auth.models

interface ConfigWrapper {
    operator fun get(key: String): String
    fun getInt(key: String): Int
    fun getBool(key: String): Boolean
}