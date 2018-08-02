package org.mrc.ide.auth.api

import org.mrc.ide.auth.api.routing.Router
import org.mrc.ide.auth.security.KeyHelper
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.serialization.DefaultSerializer
import org.slf4j.LoggerFactory
import spark.Spark
import java.net.BindException
import java.net.ServerSocket
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val appConfig = AuthApiAppConfig

    val router = Router(DefaultSerializer.instance, WebTokenHelper(appConfig.tokenIssuer,
            appConfig.tokenLifespan,
            KeyHelper("").keyPair))

    AuthApi(appConfig, router)
}

class AuthApi(private val config: AppConfig,
              private val router: Router) {

    private val logger = LoggerFactory.getLogger(AuthApi::class.java)
    private val urlBase = "/v1"

    fun run() {

        Spark.redirect.get("/", urlBase)
        setupPort()

        router.mapEndpoints(urlBase)
    }

    private fun setupPort() {
        val port = config.appPort

        var attempts = 5
        Spark.port(port)

        while (!isPortAvailable(port) && attempts > 0) {
            logger.info("Waiting for port $port to be available, $attempts attempts remaining")
            Thread.sleep(2000)
            attempts--
        }
        if (attempts == 0) {
            logger.error("Unable to bind to port $port - it is already in use.")
            exitProcess(-1)
        }
    }

    private fun isPortAvailable(port: Int): Boolean {
        try {
            ServerSocket(port).use {}
            return true
        } catch (e: BindException) {
            return false
        }
    }
}

