package org.mrc.ide.auth.security

import org.abstractj.kalium.NaCl
import org.abstractj.kalium.crypto.Password
import org.abstractj.kalium.encoders.Encoder
import org.pac4j.core.credentials.password.PasswordEncoder

class SodiumPasswordEncoder : PasswordEncoder
{
    private val passwordHelper = Password()

    override fun encode(password: String) = passwordHelper.hash(password.toByteArray(),
            Encoder.HEX,
            opsLimit(),
            memoryLimit())

    override fun matches(plainPassword: String, encodedPassword: String) = passwordHelper.verify(
            Encoder.HEX.decode(encodedPassword),
            plainPassword.toByteArray())

    // note the Password class uses scrypt with salsa under the hood,
    // hence why we're using these constants here
    private fun memoryLimit() = NaCl.Sodium.CRYPTO_PWHASH_SCRYPTSALSA208SHA256_MEMLIMIT_INTERACTIVE.toLong()

    private fun opsLimit() = NaCl.Sodium.CRYPTO_PWHASH_SCRYPTSALSA208SHA256_OPSLIMIT_INTERACTIVE
}