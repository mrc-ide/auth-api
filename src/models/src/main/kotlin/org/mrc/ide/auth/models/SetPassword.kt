package org.mrc.ide.auth.models

import org.mrc.ide.serialization.models.MinimumLength

data class SetPassword(
        @MinimumLength(8)
        val password: String
)