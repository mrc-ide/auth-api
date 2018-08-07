package org.mrc.ide.auth.api

import org.mrc.ide.auth.api.routing.ApiRouteConfig
import org.mrc.ide.auth.api.routing.Router
import org.mrc.ide.auth.security.KeyHelper
import org.mrc.ide.auth.security.WebTokenHelper
import org.mrc.ide.serialization.DefaultSerializer
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import java.net.BindException
import java.net.ServerSocket
import kotlin.system.exitProcess

import spark.Spark as spk

fun addTrailingSlashes(req: Request, res: Response) {
    if (!req.pathInfo().endsWith("/")) {
        var path = req.pathInfo() + "/"
        if (req.queryString() != null) {
            path += "?" + req.queryString()
        }
        res.redirect(path)
    }
}

fun main(args: Array<String>) {

    AuthApi().run()
}

class AuthApi {

    private val logger = LoggerFactory.getLogger(AuthApi::class.java)
    private val urlBase = "/v1"
    private val router = Router(ApiRouteConfig,
            DefaultSerializer.instance,
            tokenHelper)

    private val port = AppConfig.appPort

    fun run() {

        setupPort()

        spk.redirect.get("/", urlBase)
        spk.before("*", ::addTrailingSlashes)

        router.mapEndpoints(urlBase)
    }

    private fun setupPort() {

        var attempts = 5
        spk.port(port)

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

    companion object {
        val tokenHelper = WebTokenHelper(AppConfig.tokenIssuer,
                AppConfig.tokenLifespan,
                KeyHelper("").keyPair)
    }
}

