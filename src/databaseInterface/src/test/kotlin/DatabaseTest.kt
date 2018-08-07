import org.junit.After
import org.junit.Before
import org.mrc.ide.auth.db.DatabaseConfigFromConfigProperties

abstract class DatabaseTest {
    protected open val usesAnnex = false
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