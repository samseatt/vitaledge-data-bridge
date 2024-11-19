package com.vitaledge.databridge.controller

import com.vitaledge.databridge.service.DataBridgeService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.dataBridgeRoutes(dataBridgeService: DataBridgeService) {
    route("/notify") {
        post {
            println("POST /notify endpoint hit")

            val request = try {
                call.receive<Map<String, String>>()
            } catch (e: Exception) {
                println("Error parsing request: ${e.message}")
                return@post call.respondText(
                    "Invalid request body",
                    status = io.ktor.http.HttpStatusCode.BadRequest
                )
            }

            val destination = request["destination"] ?: return@post call.respondText(
                "destination is required",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )

            val operation = request["operation"] ?: return@post call.respondText(
                "operation is required",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )

            val source = request["source"] ?: return@post call.respondText(
                "source is required",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )

            val sourceId = request["source_id"] // Optional field

            val result = dataBridgeService.routeData(destination, operation, source, sourceId) // Suspend call
            call.respond(result)
        }
    }
}

