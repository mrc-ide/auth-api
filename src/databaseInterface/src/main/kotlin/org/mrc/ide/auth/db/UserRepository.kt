package org.mrc.ide.auth.db

import org.jooq.DSLContext
import java.sql.Timestamp
import java.time.Instant
import org.mrc.ide.auth.db.tables.AppUser.*

class UserRepository(val dsl: DSLContext){

    fun updateLastLoggedIn(username: String)
    {
        dsl.update(APP_USER)
                .set(APP_USER.LAST_LOGGED_IN, Timestamp.from(Instant.now()))
                .where(APP_USER.USERNAME.eq(username))
                .execute()
    }
}