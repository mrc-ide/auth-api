package org.mrc.ide.auth.generateDatabaseInterface

import org.jooq.util.GenerationTool

fun main(args: Array<String>)
{
    CodeGenerator().run()
}

class CodeGenerator
{
    fun run()
    {
        generate("database.xml")
    }

    private fun generate(configFileName: String)
    {
        val url = CodeGenerator::class.java.classLoader.getResource(configFileName)
        val config = url.openStream().use {
            GenerationTool.load(it)
        }
        GenerationTool().run(config)
    }
}