# 5COSC022W Client-Server Architectures – REST API Coursework

## 1. Overview of API Design
This Java EE 8 RESTful API uses JAX-RS to manage campus rooms and IoT hardware. It follows standard REST principles by using standard HTTP methods (GET, POST, PUT, DELETE) for all CRUD operations and URIs (`/rooms`, `/sensors`) for its endpoints. All data is handled in JSON format. To meet the no-database requirement, the application stores data entirely in-memory using Java `HashMap` structures.



## Step 1 – Configure Apache NetBeans
1. Open the project folder in Apache NetBeans
2. Right-click the project in the **Projects** window and select **Properties**
3. Ensure the Java Platform is set to **JDK 1.8**



## Step 2 – Build and Deploy
1. Right-click the project and select **Clean and Build** to resolve all Maven dependencies
2. Wait for the **"BUILD SUCCESS"** message in the output
3. Right-click the project and select **Run**
4. Wait for the server logs to state the project has successfully deployed to the **GlassFish 5.1.0** server



## Test 1: Create a Room (Data Initialisation)
Open **Command Prompt** or **Command Terminal** and run the following cURL to create a Room:
```
curl -i -X POST http://localhost:8080/campus-api/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"R101\", \"name\":\"Computer Lab\"}"
```
Hit **Enter**. You should see a `201 Created` status and the newly created JSON object in the response.

---

## Test 2: Add a Sensor (Dependency Validation)
Create a new request in your terminal to add a sensor:
```
curl -i -X POST http://localhost:8080/campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"S1\", \"type\":\"Temperature\", \"status\":\"ACTIVE\", \"currentValue\":22.5, \"roomId\":\"R101\"}"
```
Hit **Enter**. You should see a `201 Created` status.

---

## Test 3: Retrieve Sub-resource
Create a new request to get all sensors assigned to Room R101:
```
curl -i -X GET http://localhost:8080/campus-api/api/v1/rooms/R101/sensors
```
Hit **Enter**. You should see a `200 OK` status and the sensor data in the response.

---

## Test 4: Filter by Type
Create a new request to test the advanced search query parameters:
```
curl -i -X GET "http://localhost:8080/campus-api/api/v1/sensors?type=Temperature"
```
Hit **Enter**. You should see a `200 OK` status showing only Temperature sensors.

---

## Test 5: Delete the Room (Safety Test)
Create a final request to test the cascade cleanup/error handling:
```
curl -i -X DELETE http://localhost:8080/campus-api/api/v1/rooms/R101
```
Hit **Enter**. You should see a `409 Conflict` status because the room still contains an active sensor.



## Important Notes
- The DataStore utilises an in-memory `HashMap` — all data resets upon server restart!
- You **must** execute Test 3 (Create a Room) before Test 4, or the dependency validation will fail.
- All data transmitted and consumed exclusively in JSON format.

---

## Coursework Report Answers

### Part 1: Service Architecture & Setup
**1.1 Project & Application Configuration**
JAX-RS resource classes are Request-scoped which means the runtime instantiates a completely new instance of the class for every single incoming HTTP request, which is then destroyed once the response is sent. Because of this, any standard instance variables would be wiped out between requests, leading to immediate data loss. To maintain state without a database, the in-memory data structures must be declared as static so they belong to the class itself rather than the individual request instances. Consequently, because multiple concurrent requests will read and write to these static maps simultaneously, careful synchronisation is required to prevent race conditions and data corruption.

**1.2 The "Discovery" Endpoint**
Hypermedia as the Engine of Application State (HATEOAS) is considered the hallmark of advanced RESTful design because it makes the API self-discoverable. Instead of forcing clients to hard-code endpoint URLs, the server embeds dynamic hypermedia links directly within the JSON responses. This significantly benefits client developers because it separates the client application from the server's internal URI routing structure. If the server administrator updates a URL path, the client will not break, as it reads the routing dynamically rather than relying on static, potentially outdated documentation.

### Part 2: Room Management
**2.1 Room Resource Implementation**
Returning only IDs significantly reduces the data size, which conserves network bandwidth and speeds up the initial data transmission. However, it negatively impacts client-side processing, as the client is forced to make subsequent, sequential HTTP requests to fetch the details for each individual ID, increasing overall latency. Alternatively, returning the full room objects consumes more initial network bandwidth but is much more efficient for the client, as they receive all necessary render data in a single network round-trip.

**2.2 Room Deletion & Safety Logic**
Yes, the DELETE operation is strictly idempotent. Idempotency guarantees that making multiple identical requests has the exact same side-effect on the server as making a single request. If a client mistakenly sends the exact same DELETE request multiple times, the first request successfully removes the room (returning `204 No Content`). Subsequent requests will simply check the DataStore, realise the room no longer exists, and safely return a `404 Not Found`. The internal state of the server remains identical (the room remains deleted) regardless of how many times the request is fired.

### Part 3: Sensor Operations & Linking
**3.1 Sensor Resource & Integrity**
The @Consumes annotation acts like a strict filter for the type of data the API accepts. It creates a rule that the server will only process application/json format. If a client tries to send data in a different format (like plain text or XML), the JAX-RS system blocks the request immediately. Instead of letting the "wrong" data reach the Java code and cause a crash, the server automatically sends back an HTTP 415 (Unsupported Media Type) error. This ensures the backend only ever deals with data it is designed to understand.

**3.2 Filtered Retrieval & Search**
Path parameters are designed to identify a specific resource. However, Query parameters are specifically designed to act as filters applied against a wide collection which makes them better for filtering. If a developer needs to search by multiple criteria, query parameters easily chain together (`?type=CO2&status=ACTIVE`). Attempting to do this with path parameters would make URLs messy and difficult to manage.

### Part 4: Deep Nesting with Sub-Resources
**4.1 The Sub-Resource Locator Pattern**
The Sub-Resource Locator pattern strongly enforces code organisation. In a large API, defining deep nested paths within a single parent controller creates a massive, controller that becomes incredibly difficult to read, maintain, and test. By delegating the logic to a dedicated sub-resource class, the architecture becomes highly modularised. The parent class is solely responsible for finding the parent entity, while the sub-resource class manages all logic related to its nested data. This separation of concerns minimises code bloat and improves maintainability.

### Part 5: Advanced Error Handling, Exception Mapping & Logging
**5.2 Dependency Validation (422 Unprocessable Entity)**
An HTTP `404 Not Found` implies that the target URI endpoint itself does not exist on the server. Contrastingly, an HTTP 422 accurately informs the client that the server successfully reached the endpoint, understood the content type, and parsed the JSON syntax flawlessly. However, the  instructions contained within the data (such as linking to a `roomId` that does not exist in the system) are logically invalid. Therefore, 422 provides a much more accurate description of a reference failure.

**5.4 The Global Safety Net (500)**
Showing raw Java stack traces is a severe security risk. These act like a "blueprint" of your server, showing hackers exactly what versions of software you are using and how your internal code is structured. They can identify underlying libraries, internal Java package structures, and database drivers. An attacker can cross-reference specific metadata with public vulnerability databases to do targeted exploits against those known dependencies.

**5.5 API Request & Response Logging Filters**
Utilising JAX-RS Container Filters centralises logging, which prevents you from having to repeat the same code across the application. If logging statements are manually inserted into every method, the codebase becomes cluttered with code. Also, centralising logging within filters guarantees observability across the entire API. A filter will successfully log a request and its corresponding HTTP status code even if an exception occurs or if routing fails before the request ever reaches the intended Java method—scenarios that manual, inline logging would fail to capture.
