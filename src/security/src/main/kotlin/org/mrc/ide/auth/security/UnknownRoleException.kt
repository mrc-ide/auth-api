package org.mrc.ide.auth.security

class UnknownRoleException(roleName: String, scopePrefix: String) : Exception(
        "Unknown role with name '$roleName' and prefix '$scopePrefix'"
)