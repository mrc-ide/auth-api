package org.mrc.ide.auth.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class KeyHelper(private val keyPath: String)
{
    private val keyFactory = KeyFactory.getInstance("RSA")

    val keyPair by lazy {
        if (File(keyPath, "private_key.der").exists())
        {
            loadKeyPair()
        }
        else
        {
            logger.info("Unable to find a token keypair at ${keyPath}. Generating a new")
            logger.info("RSA keypair for token signing. If other applications need to")
            logger.info("verify tokens they should use the following public key:")
            generateKeyPair()
        }
    }

    fun loadKeyPair(): KeyPair
    {
        logger.info("Loading token signing keypair from ${keyPath}")
        return KeyPair(loadPublicKey(), loadPrivateKey())
    }

    private fun loadPublicKey(): PublicKey
    {
        val keyBytes = File(keyPath, "public_key.der").readBytes()
        val spec = X509EncodedKeySpec(keyBytes)
        return keyFactory.generatePublic(spec)
    }

    private fun loadPrivateKey(): PrivateKey
    {
        val file = File(keyPath, "private_key.der")
        val keyBytes = file.readBytes()
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return keyFactory.generatePrivate(spec)
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(KeyHelper::class.java)

        fun generateKeyPair(): KeyPair
        {
            val generator = KeyPairGenerator.getInstance("RSA").apply {
                initialize(1024)
            }
            val keypair = generator.generateKeyPair()
            val publicKey = Base64.getEncoder().encode(keypair.public.encoded)
            logger.info("Public key for token verification: " + publicKey)
            return keypair
        }
    }

}