package org.mrc.ide.auth.db

import org.jooq.DSLContext

interface TokenRepository {
    fun validateOneTimeToken(token: String): Boolean
    fun storeToken(uncompressedToken: String)
}

class JooqTokenRepository(val dsl: DSLContext): TokenRepository {
    override fun storeToken(uncompressedToken: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateOneTimeToken(token: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}