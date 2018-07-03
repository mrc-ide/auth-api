package org.mrc.ide.auth.api

import org.mrc.ide.auth.models.Config
import org.mrc.ide.auth.security.KeyHelper
import org.mrc.ide.auth.security.WebTokenHelper
import org.slf4j.LoggerFactory
import spark.Spark
import java.net.BindException
import java.net.ServerSocket
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val appConfig = Config(AuthApi::class.java.classLoader
            .getResource("config.properties")
            .path)

    val router = Router(ApiSerializer(), WebTokenHelper(appConfig, KeyHelper("").keyPair))

    AuthApi(appConfig, router)
}

class AuthApi(private val config: Config,
              private val router: Router) {

    private val logger = LoggerFactory.getLogger(AuthApi::class.java)
    private val urlBase = "/v1"

    fun run(){

        Spark.redirect.get("/", urlBase)
        setupPort()

        router.mapEndpoints(urlBase)
    }

    private fun setupPort() {
        val port = config.getInt("app.port")

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

