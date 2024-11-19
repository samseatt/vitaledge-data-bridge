plugins {
    kotlin("jvm") version "1.9.10" // Compatible with Ktor 2.3.4
    application
    id("com.github.johnrengelman.shadow") version "8.1.1" // Add Shadow plugin
}

val ktorVersion = "2.3.4"

group = "com.vitaledge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Existing dependencies
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    // Jackson support for YAML and JSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.1")


    // Ktor HTTP Client dependencies
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion") // CIO engine for HTTP Client

    // HTTP Client ContentNegotiation with Jackson
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Test dependencies
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation(kotlin("test"))

    // JUnit 5 API for writing tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")

    // JUnit 5 Engine for running the tests
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Optional: Assertions library (if needed)
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

}

application {
    mainClass.set("com.vitaledge.databridge.ApplicationKt")
}

tasks.test {
    useJUnitPlatform() // Ensures JUnit 5 tests are detected
}

tasks.withType<Jar> {
    archiveBaseName.set("vitaledge-data-bridge")
    archiveVersion.set("") // Remove version suffix
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("vitaledge-data-bridge")
    archiveClassifier.set("") // Ensures no '-all' suffix
    archiveVersion.set("")    // Ensures no version suffix
    destinationDirectory.set(file("${buildDir}/shadow"))
}

//tasks.withType<Jar> {
//    archiveBaseName.set("vitaledge-data-bridge")
//    archiveVersion.set("") // Remove version suffix
//
//    manifest {
//        attributes["Main-Class"] = "com.vitaledge.databridge.ApplicationKt"
//    }
//}
