import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.JooqTokenRepository
import org.mrc.ide.auth.db.Tables.ONETIME_TOKEN
import org.mrc.ide.auth.db.TokenRepository

class TokenRepositoryTests : RepositoryTests<TokenRepository>()
{
    override fun makeRepository(db: JooqContext) = JooqTokenRepository(db.dsl)

    @Test
    fun `can store token`()
    {
        val token = "abcdefghijklmnopqrstuvwxyz"
        givenABlankDatabase() makeTheseChanges { repo ->
            repo.storeToken(token)
        } andCheckDatabase {
            val all = it.dsl.fetch(ONETIME_TOKEN)
            assertThat(all).hasSize(1)
            assertThat(all.single()[ONETIME_TOKEN.TOKEN]).isEqualTo(token)
        }
    }

    @Test
    fun `validateOneTimeToken returns false if no token matches`()
    {
        val good = "GOOD"
        val bad = "BAD"
        givenABlankDatabase() makeTheseChanges {
            it.storeToken(bad)
        } andCheck {
            assertThat(it.validateOneTimeToken(good)).isFalse()
        }
    }

    @Test
    fun `validateOneTimeToken returns true and clears token if any token matches`()
    {
        val token = "TOKEN"
        givenABlankDatabase() makeTheseChanges {
            it.storeToken(token)
        } andCheck {
            assertThat(it.validateOneTimeToken(token)).isTrue()
            assertThat(it.validateOneTimeToken(token)).isFalse()
        }
    }
}