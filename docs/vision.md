### Formalized Vision for **DataBridge**

---

### **Overview**
**DataBridge** serves as an intermediary system that routes ingested data files from **DataGate** to their appropriate consumer pipelines, such as **DataLoader** or **RxGen**. It abstracts the complexities of data preprocessing, routing, and destination-specific configuration. By leveraging a configurable architecture, DataBridge ensures flexibility, extensibility, and consistency in data delivery.

---

### **Key Responsibilities**
1. **Routing:**
   - Match the incoming `pipeline_id` from DataGate to the appropriate consumer system (e.g., DataLoader, RxGen).
   - Deliver the data to the designated location or API endpoint based on pipeline-specific configurations.

2. **Preprocessing:**
   - Perform lightweight transformations if required (e.g., renaming files, moving files to a specified location, or chunking data).
   - Ensure data is in the correct format and location for the destination pipeline.

3. **Configuration Management:**
   - Maintain a configuration store to map `pipeline_id` to:
     - Destination system endpoints.
     - Required preprocessing steps.
     - File delivery rules (e.g., rename, move, reformat).

4. **Status Management:**
   - Track the status of data delivery and return success/failure responses to DataGate or log errors for debugging.

---

### **Proposed Architecture**

1. **Configuration Pattern:**
   - Use a **centralized configuration store** (e.g., a YAML file, database, or environment variables) to map:
     - `pipeline_id` → Destination pipeline.
     - `data_id` → Parameters needed for the pipeline’s API.
   - Example configuration schema:
     ```yaml
     pipelines:
       DataLoader:
         endpoint: "http://dataloader-service/api/v1/ingest"
         delivery_path: "/data/catalog"
         mapping:
           default_file_name: "catalog.tsv"
       RxGen:
         endpoint: "http://rxgen-service/api/v1/process"
         mapping:
           patient_id_key: "patient_id"
     ```

2. **Flow Pattern:**
   - **Incoming Notification (from DataGate):**
     - **Input:** `pipeline_id`, `data_id`, optional `kit_id`.
     - Look up `pipeline_id` in the configuration.
     - Route the data to the corresponding API endpoint, applying any preprocessing steps.
   - **Outgoing Call (to destination pipeline):**
     - Call the destination API with mapped parameters and deliver the data.

3. **Generalized Logic:**
   - Design a modular architecture where adding a new pipeline requires only configuration changes and minimal code adjustments.

---

### **DataBridge REST API**

#### Base URL:
```
/api/v1/data-bridge
```

#### Endpoints:

1. **Notify Pipeline**
   - **POST** `/notify`
   - **Description:** Receives notifications from DataGate about new data files to be routed.
   - **Request Body:**
     ```json
     {
       "pipeline_id": "string",   // ID of the target pipeline
       "data_id": "string",      // ID of the ingested data
       "kit_id": "string"        // (Optional) Contextual metadata for the destination pipeline
     }
     ```
   - **Response:**
     ```json
     {
       "status": "success",      // Or "failure"
       "message": "string"       // Details about the operation
     }
     ```

2. **Pipeline Status**
   - **GET** `/status/{pipeline_id}/{data_id}`
   - **Description:** Returns the status of a specific pipeline routing operation.
   - **Path Parameters:**
     - `pipeline_id`: The pipeline identifier.
     - `data_id`: The data file or job identifier.
   - **Response:**
     ```json
     {
       "pipeline_id": "string",
       "data_id": "string",
       "status": "completed",    // Or "in_progress", "failed"
       "message": "string"
     }
     ```

---

### **Destination Pipeline Interfaces**

#### **1. DataLoader REST API**
- **Base URL:**
  ```
  /api/v1/data-loader
  ```

- **Endpoints:**

1. **Ingest Data**
   - **POST** `/ingest`
   - **Description:** Instructs DataLoader to process and load ingested data.
   - **Request Body:**
     ```json
     {
       "data_id": "string",      // Identifier for the data
       "file_path": "string",    // Path to the data file (DataBridge delivers here)
       "metadata": {             // Any additional metadata
         "type": "catalog",      // Type of data (e.g., catalog, tab-separated file)
         "replace": true         // Whether to replace existing data
       }
     }
     ```
   - **Response:**
     ```json
     {
       "status": "accepted",     // Or "failed"
       "message": "string"
     }
     ```

---

#### **2. RxGen REST API**
- **Base URL:**
  ```
  /api/v1/rxgen
  ```

- **Endpoints:**

1. **Process Data**
   - **POST** `/process`
   - **Description:** Sends data to RxGen for patient-specific variant annotation.
   - **Request Body:**
     ```json
     {
       "data_id": "string",      // Identifier for the data
       "patient_id": "string",   // De-identified patient ID
       "file_path": "string"     // Path to the VCF file
     }
     ```
   - **Response:**
     ```json
     {
       "status": "queued",       // Or "failed"
       "message": "string"
     }
     ```

---

### **Implementation Notes**

#### 1. **Preprocessing Design**
- **Pattern:** Use a pipeline of preprocessing tasks that can be configured dynamically.
  ```kotlin
  fun preprocess(dataId: String, config: PipelineConfig) {
      moveFileToLocation(dataId, config.deliveryPath)
      renameFile(dataId, config.mapping.defaultFileName)
  }
  ```

#### 2. **Configuration Management**
- Use a lightweight configuration format (e.g., YAML or JSON) for the initial prototype.
- For production, consider a dynamic database-backed configuration.

#### 3. **Error Handling**
- Implement robust retry logic for failed API calls to pipelines.
- Log errors and expose them via the `/status` endpoint for debugging.
