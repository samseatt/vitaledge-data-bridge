# **Software Development Specification for VitalEdge DataBridge**

---

## **1. Overview**

The **VitalEdge DataBridge** microservice is a core component of the VitalEdge data ecosystem, acting as an intermediary between **DataGate** and downstream data pipelines like `RxGen` and `DataLoader`. Its primary functions include:
- Receiving data routing requests from **DataGate** via a REST API.
- Validating the request and its configuration.
- Forwarding the data to the appropriate pipeline endpoint.

This document outlines the specification for the software, including classes, functions, configurations, APIs, build system, testing, and deployment requirements. It also describes completed and upcoming phases.

---

## **2. Software Architecture**

### **Core Responsibilities**
- Accept requests to notify about data availability.
- Map the destination to the correct pipeline endpoint based on configuration.
- Forward the request to the pipeline with the necessary data details.

### **Technology Stack**
- **Language**: Kotlin (JVM)
- **Framework**: Ktor (for REST APIs and HTTP client functionality)
- **Configuration Management**: Jackson YAML
- **Build System**: Gradle (Kotlin DSL)
- **Testing Framework**: JUnit 5
- **Deployment**: Dockerized microservice on a cloud environment (e.g., AWS ECS)

---

## **3. Important Classes and Functions**

### **3.1. Application.kt**
The entry point for the application. Configures the Ktor server and sets up routing and other features.
- **Responsibilities**:
  - Start the Ktor server.
  - Load configuration.
  - Register routes.
  
---

### **3.2. ConfigLoader.kt**
Responsible for loading configuration from `application.yaml`.

- **Key Methods**:
  - `val config`: A lazily initialized configuration object that reads the YAML file into the `AppConfig` data class.

- **Configuration Schema**:
  - `server.port`: Port for the Ktor server.
  - `pipelines`: Mapping of pipeline destinations, endpoints, and operations.

---

### **3.3. DataBridgeController.kt**
Defines the REST API endpoints for the service.

- **Key Functions**:
  - `dataBridgeRoutes`: Registers the `/notify` POST endpoint.
  - **Endpoint Specification**:
    - **Path**: `/notify`
    - **Method**: POST
    - **Request Payload**:
      ```json
      {
          "destination": "RxGen",
          "operation": "annotate_vcf",
          "source": "/var/vitaledge/files/12345/sample.vcf",
          "source_id": "patient_123"
      }
      ```
    - **Response**:
      - Success Example:
        ```json
        {
            "status": "success",
            "destination": "RxGen",
            "operation": "Annotate VCF file for patient",
            "source": "/var/vitaledge/files/12345/sample.vcf",
            "source_id": "patient_123",
            "response_status": "200",
            "response_body": "Operation completed successfully."
        }
        ```
      - Error Example:
        ```json
        {
            "status": "error",
            "message": "Failed to send request to RxGen: Connection refused"
        }
        ```

---

### **3.4. DataBridgeService.kt**
Implements the core logic for routing data to the appropriate pipeline.

- **Key Functions**:
  - `suspend fun routeData(destination: String, operation: String, source: String, sourceId: String?): Map<String, String>`:
    - Maps the destination to a pipeline configuration.
    - Sends an HTTP request to the configured pipeline endpoint.
    - Captures and returns the pipeline response.

---

### **3.5. MockRxGenServer.kt (Test Only)**
A mock server for testing the DataBridge integration with the `RxGen` pipeline.

- **Key Functions**:
  - `embeddedServer`: Starts a Ktor server on port 8082 and responds to `/api/v1/process` requests.

---

## **4. Configuration Files**

### **application.yaml**
The primary configuration file for the application. Defines server settings and pipeline mappings.

- **Example**:
  ```yaml
  server:
    port: 8081

  pipelines:
    DataLoader:
      endpoint: "http://dataloader-service/api/v1/ingest"
      operations:
        update_variant_annotations: "Update variant annotations catalog"

    RxGen:
      endpoint: "http://rxgen-service/api/v1/process"
      operations:
        annotate_vcf: "Annotate VCF file for patient"
  ```

---

## **5. REST API Specification**

### **Ingress API**
- **Endpoint**: `/notify`
- **Method**: POST
- **Payload**:
  ```json
  {
      "destination": "RxGen",
      "operation": "annotate_vcf",
      "source": "/var/vitaledge/files/12345/sample.vcf",
      "source_id": "patient_123"
  }
  ```
- **Response**:
  - Success: HTTP 200 with the routed response.
  - Failure: HTTP 400 or HTTP 500 with an error message.

### **Egress API**
The DataBridge sends POST requests to pipeline endpoints (e.g., `RxGen`):
- **Request**:
  ```json
  {
      "operation": "annotate_vcf",
      "source": "/var/vitaledge/files/12345/sample.vcf",
      "source_id": "patient_123"
  }
  ```
- **Response**:
  - Success: Pipeline returns an HTTP 200 status.
  - Failure: Pipeline returns an error status.

---

## **6. Testing System**

### **Unit Tests**
- **Framework**: JUnit 5
- **Coverage**:
  - `DataBridgeService`: Tests for `routeData` with valid and invalid configurations.
  - `ConfigLoader`: Tests for loading and validating configurations.

### **Integration Tests**
- **MockRxGenServer**: Simulates the `RxGen` service.
- **Test Cases**:
  - Valid requests to `/notify` should route to the mock server.
  - Invalid requests should return error messages.

### **Commands**
- Run all tests:
  ```bash
  ./gradlew test
  ```

---

## **7. Build System**

### **Build Configuration**
- **Tool**: Gradle (Kotlin DSL)
- **Key Tasks**:
  - `clean`: Cleans the build directory.
  - `build`: Compiles the project and runs tests.
  - `run`: Starts the application.
- **Build Command**:
  ```bash
  ./gradlew clean build
  ```

---

## **8. Production Deployment**

### **Docker Configuration**
The service is Dockerized for deployment in a cloud environment like AWS.

#### **Dockerfile**:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/vitaledge-data-bridge.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **Build Docker Image**:
```bash
docker build -t vitaledge-data-bridge .
```

#### **Run Docker Container**:
```bash
docker run -p 8081:8081 vitaledge-data-bridge
```

---

## **9. Project Phases**

### **Phase 1 (Completed)**
- Initial implementation of the DataBridge:
  - Configuration management via `application.yaml`.
  - REST API for `/notify` endpoint.
  - Core routing logic in `DataBridgeService`.
  - Unit and integration tests using JUnit 5 and Ktor.

### **Phase 2 (Planned Features)**
1. **Enhanced Features**:
   - Support for unzipping and transforming files before routing.
   - Support for additional pipelines.
2. **Good Housekeeping**:
   - Implement logging with structured logs (e.g., JSON format).
   - Add health check endpoints.
3. **Performance Improvements**:
   - Implement circuit breakers and retry logic for egress API calls.

---

This document serves as a comprehensive development specification for the **VitalEdge DataBridge** microservice, ensuring developers have clear guidelines for extending and maintaining the project. For further details, refer to the design and onboarding documents.