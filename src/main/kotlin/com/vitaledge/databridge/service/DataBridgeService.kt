package com.vitaledge.databridge.service

import com.vitaledge.databridge.config.ConfigLoader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import java.nio.channels.UnresolvedAddressException

class DataBridgeService {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson() // Use Jackson for JSON serialization
        }
    }

    suspend fun routeData(destination: String, operation: String, source: String, sourceId: String?): Map<String, String> {
        println("Routing data: destination=$destination, operation=$operation, source=$source, sourceId=$sourceId")

        val pipelineConfig = ConfigLoader.config.pipelines[destination]
            ?: return mapOf("status" to "error", "message" to "Destination not found: $destination")

        val operationName = pipelineConfig.operations[operation]
            ?: return mapOf("status" to "error", "message" to "Operation not supported: $operation for $destination")

        // Prepare request payload
        val requestPayload = mapOf(
            "operation" to operation,
            "source" to source,
            "source_id" to sourceId
        )

        return try {
            // Send request to the destination endpoint
            val response: HttpResponse = httpClient.post(pipelineConfig.endpoint) {
                contentType(ContentType.Application.Json) // Set JSON Content-Type
                setBody(requestPayload) // Automatically serialized to JSON
            }

            // Handle response
            mapOf(
                "status" to "success",
                "destination" to destination,
                "operation" to operationName,
                "source" to source,
                "source_id" to (sourceId ?: "N/A"),
                "response_status" to response.status.value.toString(),
                "response_body" to response.bodyAsText()
            )
        } catch (e: UnresolvedAddressException) {
            println("Unresolved address for destination: ${pipelineConfig.endpoint}")
            mapOf(
                "status" to "error",
                "message" to "Failed to send request to $destination: Unresolved address ${pipelineConfig.endpoint}"
            )
        } catch (e: Exception) {
            println("Error communicating with destination: ${e.message}")
            e.printStackTrace()
            mapOf(
                "status" to "error",
                "message" to "Failed to send request to $destination: ${e.message}"
            )
        }
    }
}
