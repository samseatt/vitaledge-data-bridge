# **VitalEdge DataBridge**

## **Overview**

**VitalEdge DataBridge** is a Kotlin-based microservice within the **VitalEdge ecosystem** that acts as an intermediary between the **VitalEdge DataGate** microservice and various downstream data processing pipelines, such as `RxGen` and `DataLoader`. Its primary function is to route validated data to the appropriate destination pipelines based on configuration.

This service is part of the broader **VitalEdge DataHarbor** concept, which centralizes data ingestion, validation, routing, and scheduling for the ecosystem.

---

## **Key Features**

- **Data Routing**: Routes validated data to downstream pipelines based on configuration.
- **REST API**: Exposes endpoints for notifying the service about data readiness.
- **Configurable Pipelines**: Supports dynamic configuration for multiple pipeline destinations.
- **Integration Ready**: Built to interact with other VitalEdge microservices via REST APIs.

---

## **Project Structure**

```
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── com.vitaledge.databridge/
│   │   │   │   ├── Application.kt          # Main entry point
│   │   │   │   ├── controller/
│   │   │   │   │   ├── DataBridgeController.kt  # REST endpoints
│   │   │   │   ├── service/
│   │   │   │   │   ├── DataBridgeService.kt    # Core business logic
│   │   │   │   ├── config/
│   │   │   │   │   ├── ConfigLoader.kt         # Configuration management
│   ├── test/
│   │   ├── kotlin/
│   │   │   ├── com.vitaledge.databridge/
│   │   │   │   ├── DataBridgeIntegrationTest.kt # Integration tests
│   │   │   │   ├── MockRxGenServer.kt          # Mock server for testing
├── build.gradle.kts               # Gradle build script
├── application.yaml                # Configuration file
├── Dockerfile                      # Dockerfile for packaging the service
├── README.md                       # This README file
```

---

## **Getting Started**

### **Prerequisites**

1. **Java Development Kit (JDK)**:
   - Version: 17 or higher.
   - Verify installation:
     ```bash
     java -version
     ```

2. **Gradle**:
   - Gradle wrapper is included in the project; global installation is optional.
   - Verify Gradle installation (if needed):
     ```bash
     gradle -v
     ```

3. **Kotlin**:
   - This project is developed in Kotlin, version `1.8.22`.

4. **Docker** (Optional):
   - Required if running the service as a container.

---

### **Running Locally**

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd vitaledge-data-bridge
   ```

2. **Build the Project**:
   ```bash
   ./gradlew clean build
   ```

3. **Run the Service**:
   ```bash
   ./gradlew run
   ```

4. **Access the Service**:
   - The service will run on `http://localhost:8081`.

---

### **Testing**

**Unit and Integration Tests**:
Run all tests using Gradle:
```bash
./gradlew test
```

**Mock Server Testing**:
Start the mock server for testing integration with `RxGen`:
```bash
java -cp build/classes/kotlin/main com.vitaledge.databridge.MockRxGenServerKt
```

---

### **Docker**

1. **Build the Docker Image**:
   ```bash
   docker build -t vitaledge-data-bridge .
   ```

2. **Run the Docker Container**:
   ```bash
   docker run -p 8081:8081 --name data-bridge vitaledge-data-bridge
   ```

3. **Test the Container**:
   Use `curl` or Postman to interact with the service running in Docker:
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

---

## **Configuration**

### **application.yaml**
The configuration file is located in `src/main/resources/application.yaml`.

#### Example Configuration:
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

## **REST API**

### **1. Notify Endpoint**
- **Path**: `/notify`
- **Method**: POST
- **Description**: Notifies the service of data availability and routes it to the specified pipeline.
- **Request Payload**:
  ```json
  {
      "destination": "RxGen",
      "operation": "annotate_vcf",
      "source": "/var/vitaledge/files/67890/patient.vcf",
      "source_id": "patient_123"
  }
  ```
- **Response**:
  - **Success**:
    ```json
    {
        "status": "success",
        "destination": "RxGen",
        "operation": "Annotate VCF file for patient",
        "source": "/var/vitaledge/files/67890/patient.vcf",
        "source_id": "patient_123",
        "response_status": "200",
        "response_body": "Operation completed successfully."
    }
    ```
  - **Error**:
    ```json
    {
        "status": "error",
        "message": "Failed to send request to RxGen: Connection refused"
    }
    ```

---

## **Development Notes**

### **Key Kotlin Features**
- **Coroutines**:
  - Asynchronous processing using Kotlin's lightweight coroutines.
- **Data Classes**:
  - Immutable data representation for clean code.
- **Ktor Framework**:
  - Lightweight server framework used for REST API and HTTP client.

---

## **Contributing**

1. Fork the repository.
2. Create a feature branch.
3. Commit changes and push to your fork.
4. Open a pull request to the main repository.

---

## **License**

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## **Contact**

For any questions or issues, please contact the VitalEdge team at **support@vitaledge.com**.

--- 