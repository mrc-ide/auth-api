import org.mrc.ide.auth.db.DatabaseConfig
import org.mrc.ide.auth.db.DatabaseConfigFromConfigProperties
import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.UnableToConnectToDatabase

class DatabaseCreationHelper(private val config: DatabaseConfig) {

    private var error: Exception? = null

    fun createTemplateFromDatabase() {
        println("Planning to create template ${config.templateName} from ${config.name}")
        checkDatabaseExists(config.name)
        if (databaseExists(config.templateName, maxAttempts = 1)) {
            println("Template database already exists")
        } else {
            JooqContext("postgres").use {
                it.dsl.query("ALTER DATABASE ${config.name} RENAME TO ${config.templateName}").execute()
            }
            println("Created template database by renaming ${config.name} to ${config.templateName}")
            checkDatabaseExists(config.templateName)
        }
    }

    fun restoreDatabaseFromTemplate() {
        if (databaseExists(config.templateName, maxAttempts = 1)) {
            JooqContext("postgres").use {
                it.dsl.query("ALTER DATABASE ${config.templateName} RENAME TO ${config.name}").execute()
            }
            checkDatabaseExists(config.name)
        } else {
            println("Template database does not exist; skipping")
        }
    }

    fun createDatabaseFromTemplate() {
        JooqContext("postgres").use {
            it.dsl.query("CREATE DATABASE ${config.name} TEMPLATE ${config.templateName};").execute()
        }
        DatabaseCreationHelper(config).checkDatabaseExists(config.name)
    }

    fun dropDatabase() {
        JooqContext("postgres").use {
            it.dsl.query("DROP DATABASE ${config.name}").execute()
        }
    }

    fun checkDatabaseExists(dbName: String) {
        if (!databaseExists(dbName)) {
            throw error!!
        }
    }

    private fun databaseExists(dbName: String, maxAttempts: Int = 10): Boolean {
        print("Checking that database '$dbName' exists...")
        var attemptsRemaining = maxAttempts
        while (attemptsRemaining > 0) {
            if (check(dbName)) {
                println("✔")
                return true
            } else {
                attemptsRemaining--
                if (attemptsRemaining > 0) {
                    println("Unable to connect. I will wait and then retry $attemptsRemaining more times")
                    Thread.sleep(2000)
                }
            }
        }
        return false
    }

    fun check(dbName: String): Boolean {
        // We expect the connection to fail if the db doesn't exist, so disable
        // logging from the postgres library, as it spews out tons of error messages
        // in that case.
        return disableLoggingFrom("org.postgresql") {
            try {
                JooqContext(dbName).close()
                true
            } catch (e: UnableToConnectToDatabase) {
                error = e
                false
            }
        }
    }
}