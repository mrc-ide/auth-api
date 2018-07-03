package org.mrc.ide.auth.api

import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import org.mrc.ide.auth.models.User
import java.time.Instant
import java.time.LocalDate

interface Serializer
{
    fun toJson(result: Any?): String
    fun <T> fromJson(json: String, klass: Class<T>): T
    fun convertFieldName(name: String): String

    val gson: Gson
}

class ApiSerializer: Serializer {
    private val toDateStringSerializer = jsonSerializer<Any> {
        JsonPrimitive(it.src.toString())
    }

    companion object {
        val instance: Serializer = ApiSerializer()
    }

    override val gson: Gson

    init {
        val common = GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .setFieldNamingStrategy { convertFieldName(it.name) }
                .serializeNulls()

                .registerTypeAdapter<Instant>(toDateStringSerializer)
                .registerTypeAdapter<LocalDate>(toDateStringSerializer)


        // Some serializers for complex objects need to recurse back to the default
        // serialization strategy. So we separate out a Gson object that has all the
        // primitive serializers, and then create one that extends it with the complex
        // serializers.
        val baseGson = common.create()
        gson = common
                .registerTypeAdapter<User>(ruleBasedSerializer(baseGson))
                .create()
    }

    override fun toJson(result: Any?): String = gson.toJson(result)
    override fun <T> fromJson(json: String, klass: Class<T>): T = gson.fromJson(json, klass)

    override fun convertFieldName(name: String): String {
        val builder = StringBuilder()
        for (char in name) {
            if (char.isUpperCase()) {
                builder.append("_" + char.toLowerCase())
            } else {
                builder.append(char)
            }
        }
        return builder.toString().trim('_')
    }

}