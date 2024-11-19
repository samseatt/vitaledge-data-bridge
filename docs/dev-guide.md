# Developer’s Guide to the VitalEdge Kotlin Projects and DataBridge Service

Welcome to the **VitalEdge Kotlin Projects**. This guide is designed to help you get started with the **DataBridge Service**, part of the **VitalEdge ecosystem**, and to familiarize you with Kotlin, Gradle, IntelliJ, and Ktor. It also includes explanations of the tools, patterns, and Kotlin-specific features used in the project, to equip you for contributing effectively.

---

## **1. Overview of the DataBridge Project**

The **DataBridge Service** is a Kotlin-based microservice that acts as an intermediary between **DataGate** (a data-fetching and validation service) and various destination pipelines (e.g., `RxGen`, `DataLoader`). Its primary role is to:
1. Accept data routing requests from **DataGate** via REST endpoints.
2. Process these requests, including validating configurations and mapping operations.
3. Forward the data to its intended pipeline (e.g., `RxGen`) through HTTP calls.

This service is built using **Kotlin**, **Gradle**, and **Ktor**.

---

## **2. Setting Up the Project**

### **Prerequisites**
1. **IntelliJ IDEA Ultimate Edition**
   - Kotlin projects and advanced features are fully supported in IntelliJ IDEA Ultimate. Download it [here](https://www.jetbrains.com/idea/).
2. **Java Development Kit (JDK)**
   - Install JDK 17 or higher (required by Gradle and Kotlin).
   - Verify installation:
     ```bash
     java -version
     ```
3. **Gradle**
   - Gradle is included in the project wrapper, but installing Gradle globally is optional. Verify Gradle version:
     ```bash
     gradle -v
     ```
4. **Curl/Postman**
   - Use `curl` or Postman to test REST endpoints.

---

### **Project Setup in IntelliJ**
1. **Clone the Repository**:
   Clone the project repository into a local directory:
   ```bash
   git clone <repository-url>
   cd vitaledge-data-bridge
   ```

2. **Open the Project in IntelliJ**:
   - Open IntelliJ IDEA and select **Open Project**.
   - Navigate to the cloned repository and open the project.

3. **Verify Gradle Configuration**:
   - IntelliJ should automatically detect the `build.gradle.kts` file.
   - Ensure that IntelliJ uses the correct Gradle JDK:
     1. Go to **File > Project Structure > Project**.
     2. Ensure the Project SDK is set to JDK 17 or higher.

4. **Sync Gradle**:
   - IntelliJ will automatically sync the Gradle project.
   - If not, open the **Gradle Tool Window** and click the **Refresh** button.

5. **Run the Application**:
   - Navigate to `Application.kt` and run the application.
   - Ensure the service starts successfully on the specified port.

---

## **3. Project Structure**

### **Key Directories**
- `src/main/kotlin`: Contains all production code.
  - `config/`: Configuration handling (e.g., loading `application.yaml`).
  - `controller/`: REST endpoint definitions.
  - `service/`: Core business logic, including routing to pipelines.
- `src/main/resources`: Contains configuration files (`application.yaml`).
- `src/test/kotlin`: Contains all test code, including mock servers and integration tests.

### **Gradle Build Script**
The `build.gradle.kts` file contains:
- Project dependencies (e.g., `ktor-server`, `ktor-client`).
- Plugin configurations for building and running the project.

---

## **4. Key Tools and Libraries**

### **Kotlin**
Kotlin is a modern, concise, and expressive programming language. It integrates seamlessly with Java and introduces features like:
- Null safety (`String?` vs. `String`).
- Default and named arguments in functions.
- Coroutines for asynchronous programming.

### **Gradle**
Gradle is used as the build system:
- **Plugins**: `application`, `kotlin`.
- **Dependency Management**: Manages library versions and integrates with Kotlin DSL.
- **Tasks**:
  - `./gradlew run`: Runs the application.
  - `./gradlew test`: Runs unit and integration tests.

### **Ktor**
Ktor is a Kotlin framework for building asynchronous servers and clients:
- **Server**: Provides a lightweight framework for creating REST APIs.
- **Client**: Simplifies making HTTP calls, used to forward data to pipelines.

---

## **5. Key Kotlin Concepts**

### **Kotlin-Specific Code Features**
1. **Data Classes**:
   - Simplifies defining immutable objects with boilerplate features like `equals()`, `hashCode()`, and `toString()`:
     ```kotlin
     data class PipelineConfig(
         val endpoint: String,
         val deliveryPath: String?,
         val operations: Map<String, String>
     )
     ```

2. **Null Safety**:
   - Kotlin distinguishes nullable types (`String?`) from non-nullable types (`String`), preventing null pointer exceptions:
     ```kotlin
     val sourceId: String? = request["source_id"] // Nullable type
     ```

3. **Coroutines**:
   - Kotlin's lightweight threads, used for handling asynchronous tasks:
     ```kotlin
     suspend fun routeData() {
         val response = httpClient.post(pipelineConfig.endpoint) {
             setBody(requestPayload)
         }
     }
     ```

4. **Extension Functions**:
   - Add new methods to existing classes:
     ```kotlin
     fun String.capitalizeFirst(): String {
         return this.replaceFirstChar { it.uppercase() }
     }
     ```

---

### **Libraries and Packages**

#### **1. Ktor**
- `io.ktor.server`: For building the REST server.
- `io.ktor.client`: For HTTP requests to other services.
- Key plugins:
  - **Routing**: For defining REST endpoints.
  - **ContentNegotiation**: For JSON serialization using Jackson.
  - **Netty Engine**: To run the server.

#### **2. Jackson**
Used for JSON serialization/deserialization:
- Automatically maps Kotlin data classes to JSON and vice versa.

---

## **6. Running and Testing the Application**

### **Run the Application**
Start the DataBridge application:
```bash
./gradlew run
```

### **Run the Mock Server**
Start the mock server for `RxGen`:
```bash
java -cp build/classes/kotlin/main com.vitaledge.databridge.MockRxGenServerKt
```

### **Testing with `curl`**
Send a POST request to the `/notify` endpoint:
```bash
curl -X POST http://localhost:8081/notify \
    -H "Content-Type: application/json" \
    -d '{
        "destination": "RxGen",
        "operation": "annotate_vcf",
        "source": "/var/vitaledge/files/67890/patient.vcf",
        "source_id": "patient_123"
    }'
```

### **Run Unit Tests**
Run all tests:
```bash
./gradlew test
```

Run a specific test:
```bash
./gradlew test --tests com.vitaledge.databridge.DataBridgeIntegrationTest
```

---

## **7. Common Challenges and Tips**

### **1. Dependency Issues**
- Ensure Gradle dependencies are up-to-date and compatible.
- Refresh Gradle in IntelliJ after modifying `build.gradle.kts`.

### **2. Port Conflicts**
- Use unique ports for running multiple services (e.g., 8081 for DataBridge, 8082 for MockRxGen).

### **3. Debugging**
- Use IntelliJ’s built-in debugger to step through code.
- Add meaningful log messages using `println` or a logging library like SLF4J.

---

## **8. Where to Learn More**

- **Kotlin Documentation**: [https://kotlinlang.org/docs/home.html](https://kotlinlang.org/docs/home.html)
- **Gradle Documentation**: [https://gradle.org/docs](https://gradle.org/docs)
- **Ktor Documentation**: [https://ktor.io/docs](https://ktor.io/docs)

---
