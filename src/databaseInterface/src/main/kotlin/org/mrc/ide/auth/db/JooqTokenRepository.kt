package org.mrc.ide.auth.db

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.mrc.ide.auth.db.Tables.*
import java.sql.Timestamp
import java.time.Instant
import org.mrc.ide.auth.models.*
import org.mrc.ide.auth.models.permissions.*

interface TokenRepository {
    fun validateOneTimeToken(token: String): Boolean
}

class JooqTokenRepository(val dsl: DSLContext): TokenRepository {

    override fun validateOneTimeToken(token: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}