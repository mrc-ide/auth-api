package org.mrc.ide.auth.db

class UnableToConnectToDatabase(val url: String) : Exception(
        "Unable to connect to database at $url"
)