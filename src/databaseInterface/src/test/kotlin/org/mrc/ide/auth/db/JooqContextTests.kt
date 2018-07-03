package org.mrc.ide.auth.db

import org.junit.Test

class JooqContextTests {

    @Test
    fun canConnect() {
        JooqContext().use {

        }
    }
}