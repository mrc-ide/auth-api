package integrationtests

import org.junit.After
import org.junit.Before
import org.mrc.ide.auth.db.DatabaseConfigFromConfigProperties
import org.mrc.ide.auth.db.DatabaseCreationHelper

abstract class IntegrationTest {

    val requestHelper = RequestHelper()
    private val databaseCreationHelper = DatabaseCreationHelper(DatabaseConfigFromConfigProperties)

    @Before
    fun createDatabase() {
        databaseCreationHelper.createTemplateFromDatabase()
        databaseCreationHelper.createDatabaseFromTemplate()
    }

    @After
    fun dropDatabase() {
        databaseCreationHelper.dropDatabase()
        databaseCreationHelper.restoreDatabaseFromTemplate()
    }
}