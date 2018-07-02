package org.mrc.ide.auth.security

class MissingTokenKeyPairException(val paths: List<String>)
    : Exception("Unable to find a keypair at any of:\n" + paths.joinToString("\n"))