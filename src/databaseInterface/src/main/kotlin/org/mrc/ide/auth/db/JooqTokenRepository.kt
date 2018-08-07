package org.mrc.ide.auth.db

import org.jooq.DSLContext
import org.mrc.ide.auth.db.Tables.ONETIME_TOKEN

interface TokenRepository: Repository {
    fun validateOneTimeToken(uncompressedToken: String): Boolean
    fun storeToken(uncompressedToken: String)
}

class JooqTokenRepository(val dsl: DSLContext): TokenRepository {

    constructor() : this(JooqContext().dsl)

    override fun storeToken(uncompressedToken: String)
    {
        dsl.newRecord(ONETIME_TOKEN).apply {
            this.token = uncompressedToken
        }.store()
    }

    override fun validateOneTimeToken(uncompressedToken: String): Boolean
    {
        val deletedCount = dsl.deleteFrom(ONETIME_TOKEN)
                .where(ONETIME_TOKEN.TOKEN.eq(uncompressedToken))
                .execute()
        return deletedCount == 1
    }

}