package org.mrc.ide.auth.api.security

import org.mrc.ide.auth.db.JooqTokenRepository
import org.mrc.ide.auth.db.TokenRepository
import org.mrc.ide.auth.security.OneTimeTokenChecker


class JooqOneTimeTokenChecker(val tokenRepository: TokenRepository = JooqTokenRepository())
    : OneTimeTokenChecker
{
    override fun checkToken(uncompressedToken: String): Boolean
    {
        // This transaction is immediately committed, regardless of result
        return tokenRepository.validateOneTimeToken(uncompressedToken)
    }
}