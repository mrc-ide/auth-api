package org.mrc.ide.auth.models

import java.io.File
import java.io.FileNotFoundException
import java.util.*

open class Config(path: String) : ConfigWrapper {

    private val properties = Properties().apply {
        val file = File(path)
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
        else{
            throw FileNotFoundException("Unable to load $path")
        }
    }

    override operator fun get(key: String): String {
        val x = properties[key]
        if (x != null) {
            return x as String
        } else {
            throw MissingConfigurationKey(key)
        }
    }

    override fun getInt(key: String) = get(key).toInt()
    override fun getBool(key: String) = get(key).toBoolean()
}

class MissingConfigurationKey(val key: String) : Exception("Missing configuration key '$key'")