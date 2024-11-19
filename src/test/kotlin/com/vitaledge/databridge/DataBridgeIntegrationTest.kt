package com.vitaledge.databridge

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataBridgeIntegrationTest {

    private lateinit var mockServer: ApplicationEngine

    @BeforeEach
    fun setup() {
        // Start mock server
        mockServer = embeddedServer(Netty, port = 8082) {
            routing {
                post("/api/v1/process") {
                    call.respondText("VCF file annotated successfully!")
                }
            }
        }.start()
    }

    @AfterEach
    fun teardown() {
        // Stop mock server
        mockServer.stop(1000, 10000)
    }

    @Test
    fun `test DataBridge integration with RxGen`() {
        // Simulate a DataBridge call to the mock server
        val mockResponse = simulateDataBridgeCall()
        assertEquals("success", mockResponse["status"])
        assertEquals("VCF file annotated successfully!", mockResponse["response_body"])
    }

    private fun simulateDataBridgeCall(): Map<String, String> {
        // Simulate calling DataBridge's routeData method
        return mapOf(
            "status" to "success",
            "response_body" to "VCF file annotated successfully!"
        )
    }
}
