package com.vitaledge.databridge

import com.vitaledge.databridge.controller.dataBridgeRoutes
import com.vitaledge.databridge.service.DataBridgeService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {} // Jackson handles JSON serialization and deserialization
    }

    val dataBridgeService = DataBridgeService()
    routing {
        dataBridgeRoutes(dataBridgeService)
    }
}
