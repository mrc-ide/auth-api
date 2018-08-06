package org.mrc.ide.auth.emails

import com.github.mustachejava.DefaultMustacheFactory
import org.eclipse.jetty.util.Loader.getResource
import java.io.StringWriter

abstract class MustacheEmail : EmailData
{
    abstract val textTemplate: String
    abstract val htmlTemplate: String
    abstract val values: Map<String, String>

    override final fun text() = realize(textTemplate)
    override final fun html() = realize(htmlTemplate)

    private fun realize(templateFileName: String): String
    {
        return StringWriter().use { output ->
            val templateURI = getResource("templates/$templateFileName")
            templateURI.openStream().bufferedReader().use { input ->
                DefaultMustacheFactory()
                        .compile(input, "email")
                        .execute(output, values)
                output.toString()
            }
        }
    }
}