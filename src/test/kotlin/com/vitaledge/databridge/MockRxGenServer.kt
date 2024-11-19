package com.vitaledge.databridge

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8082) { // Mock RxGen pipeline server
        routing {
            post("/api/v1/process") {
                call.respondText("VCF file annotated successfully!")
            }
        }
    }.start(wait = true)
}
