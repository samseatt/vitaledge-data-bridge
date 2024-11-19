package com.vitaledge.databridge.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

data class PipelineConfig(
    val endpoint: String,
    val deliveryPath: String?,
    val operations: Map<String, String>
)

data class AppConfig(
    val server: ServerConfig,
    val pipelines: Map<String, PipelineConfig>
)

data class ServerConfig(val port: Int)

object ConfigLoader {
    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val config: AppConfig by lazy {
        val file = File("src/main/resources/application.yaml")
        if (!file.exists()) {
            throw RuntimeException("Configuration file not found at: ${file.absolutePath}")
        }

        try {
            println("Loading configuration from: ${file.absolutePath}")
            mapper.readValue(file, AppConfig::class.java)
        } catch (e: Exception) {
            println("Error reading configuration: ${e.message}")
            throw RuntimeException("Error parsing configuration file: ${file.absolutePath}", e)
        }
    }
}
