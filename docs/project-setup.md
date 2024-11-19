## DataBridge Project Setup

Here’s how you can set up your development environment for **VitalEdge DataBridge** step-by-step:

---

### **Step 1: Setting Up IntelliJ IDEA**
#### **1. Install IntelliJ IDEA**
- If not installed, download the Community Edition (free) or Ultimate Edition (paid) from [JetBrains IntelliJ IDEA](https://www.jetbrains.com/idea/download/).

#### **2. Install Kotlin Plugin**
- The Kotlin plugin is pre-installed with IntelliJ IDEA, but you can verify it:
  - Go to **File > Settings > Plugins** (or **Preferences > Plugins** on macOS).
  - Search for "Kotlin" and ensure it is installed and updated.

#### **3. Configure JDK**
- Kotlin runs on the JVM, so you’ll need a Java Development Kit (JDK):
  - Download the latest **JDK (17 or later)** from [AdoptOpenJDK](https://adoptium.net/).
  - In IntelliJ:
    - Go to **File > Project Structure > SDKs**.
    - Add the JDK path.

---

### **Step 2: Creating the DataBridge Project**
#### **1. Start a New Kotlin Project**
- Open IntelliJ IDEA.
- Select **File > New Project**.
- Choose **Kotlin > Kotlin/Java** as the project type.
- Select **JVM** as the Kotlin target platform.
- Choose a JDK (from the dropdown configured earlier).
- Project Name: `vitaledge-data-bridge`.

#### **2. Add Build Tool: Gradle**
- In the project setup wizard:
  - Select **Gradle** as the build tool (recommended for modern Kotlin projects).
  - Enable **Kotlin DSL** (you’ll work with `build.gradle.kts` instead of `build.gradle`).

#### **3. Directory Structure**
IntelliJ will create the following directory structure:
```
vitaledge-data-bridge/
├── src/
│   ├── main/
│   │   ├── kotlin/   # Kotlin source files
│   │   └── resources/ # Application resources (e.g., configs)
│   ├── test/
│   │   ├── kotlin/   # Unit test source files
│   │   └── resources/
├── build.gradle.kts   # Gradle build script
├── settings.gradle.kts
└── gradle/
```

---

### **Step 3: Gradle Build Configuration**
Edit the `build.gradle.kts` to include necessary dependencies:

#### **Basic `build.gradle.kts`**
```kotlin
plugins {
    kotlin("jvm") version "1.9.0" // Use the latest version
    application
}

group = "com.vitaledge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))

    // Logging (SLF4J + Logback)
    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("ch.qos.logback:logback-classic:1.4.0")

    // HTTP Client for REST API interactions
    implementation("io.ktor:ktor-client-core:2.2.0")
    implementation("io.ktor:ktor-client-cio:2.2.0") // Asynchronous HTTP Client

    // Configuration library (e.g., reading YAML/JSON configs)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

    // Testing
    testImplementation(kotlin("test")) // Kotlin test framework
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

application {
    mainClass.set("com.vitaledge.databridge.ApplicationKt")
}

tasks.test {
    useJUnitPlatform() // Use JUnit 5 for testing
}
```

---

### **Step 4: Run a Simple Kotlin Application**
1. **Create the Main Entry Point:**
   - Create a new Kotlin file under `src/main/kotlin/`:
     ```
     src/main/kotlin/com/vitaledge/databridge/Application.kt
     ```

2. **Add Basic Code:**
   ```kotlin
   package com.vitaledge.databridge

   fun main() {
       println("Welcome to VitalEdge DataBridge!")
   }
   ```

3. **Run the Application:**
   - Click the green **Run** button in IntelliJ.
   - You should see `Welcome to VitalEdge DataBridge!` in the console.

---

### **Step 5: IDE Tips for Java Developers**

#### **Kotlin vs Java: Key Features**
1. **Conciseness:**
   - No need for semicolons (`;`).
   - Type inference reduces boilerplate (e.g., `val name = "John"` instead of `String name = "John";`).
   - Getters and setters are replaced by `val` (read-only) or `var` (mutable).

2. **Null Safety:**
   - Kotlin enforces null safety at compile time. Use `?` for nullable types:
     ```kotlin
     var nullableString: String? = null // Nullable type
     ```

3. **Functional Programming:**
   - Kotlin supports higher-order functions and lambdas, making it great for concise, readable code.

4. **Extension Functions:**
   - Add functionality to existing classes without inheritance.
     ```kotlin
     fun String.isEmail(): Boolean = this.contains("@")
     ```

#### **IntelliJ Tips:**
1. Use **Alt+Enter** for quick fixes and importing missing classes.
2. Navigate between files and methods using **Ctrl+Click**.
3. Use **Ctrl+Shift+T** to create test cases automatically.

---

### **Step 6: Next Steps**
1. **Confirm Your Setup:**
   - Run the sample Kotlin application to confirm your environment is working.
2. **Next Phase: Start Building the Core of DataBridge:**
   - Build the configuration loader and the initial structure for REST endpoints.