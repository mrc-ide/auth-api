import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.Repository

data class RepositoryTestConfig<TRepository : Repository>(
        val makeRepository: (JooqContext) -> TRepository,
        val populateDatabase: ((JooqContext) -> Unit),
        val checkRepository: ((TRepository) -> Unit)? = null,
        val changeViaRepository: ((TRepository) -> Unit)? = null,
        val checkDatabase: ((JooqContext) -> Unit)? = null
)
{
    infix fun makeTheseChanges(changeViaRepository: (TRepository) -> Unit) = this.copy(changeViaRepository = changeViaRepository)

    infix fun check(checkRepository: (TRepository) -> Unit)
    {
        val config = this.copy(checkRepository = checkRepository)
        RepositoryTestRunner(config).run()
    }

    infix fun andCheck(checkRepository: (TRepository) -> Unit) = check(checkRepository)

    infix fun andCheckDatabase(checkDatabase: (JooqContext) -> Unit)
    {
        val config = this.copy(checkDatabase = checkDatabase)
        RepositoryTestRunner(config).run()
    }
}


class RepositoryTestRunner<TRepository : Repository>(config: RepositoryTestConfig<TRepository>)
{
    val makeRepository = config.makeRepository
    val populateDatabase = config.populateDatabase
    val checkRepository = config.checkRepository ?: {}
    val changeViaRepository = config.changeViaRepository ?: {}
    val checkDatabase = config.checkDatabase ?: {}

    fun run()
    {
        JooqContext().use { populateDatabase(it) }
        JooqContext().use {
            val repo = makeRepository(it)
            changeViaRepository(repo)
            checkRepository(repo)
        }
        JooqContext().use { checkDatabase(it) }
    }
}