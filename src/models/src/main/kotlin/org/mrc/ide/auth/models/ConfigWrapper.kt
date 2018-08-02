package org.mrc.ide.auth.models

interface ConfigWrapper {
    val tokenIssuer: String
    val tokenLifespan: Long
}