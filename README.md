# trainer-hours-service (Spring Boot, REST API, & Microservice)

This microservice is a dedicated component of the Gym CRM system, responsible for managing and tracking the workload of trainers. It provides a simple REST API for other services to add, delete, or retrieve training duration data.

## Features

- **Dedicated Microservice:** A standalone service that encapsulates trainer workload management, promoting a single responsibility principle.
- **RESTful API:** Exposes endpoints for managing trainer workload data, including `update` and `get` operations.
- **Service Discovery:** Registers with a central Eureka server, allowing other services (like `gym-crm`) to dynamically discover and communicate with it using a Feign client.
- **Robust Exception Handling:** Implements a global exception handler to provide consistent and informative API error responses.
- **Persistence:** Stores trainer workload data in a relational database using Spring Data JPA. A custom `AttributeConverter` is used to serialize complex map-based data into a single database column.
- **API Documentation:** The API is documented with OpenAPI 3 (Swagger), providing clear and interactive documentation for developers.
- **Logging:** A custom request logging filter captures and logs details of incoming requests and outgoing responses for enhanced debugging and observability.
- **Security:** Integrated with JWT-based security to ensure that only authenticated requests can access its endpoints.

## Technologies

- **Java 17+**
- **Spring Boot**
- **Spring Cloud Netflix (for Eureka and Feign)**
- **Spring Data JPA**
- **Spring Security (with JWT)**
- **PostgreSQL**
- **Lombok**
- **JUnit 5 & Mockito**
- **SLF4J & Log4j2**

---

## How to Run

To run this microservice, you must first start the Eureka server.

**Run Order:**

1.  **`discovery-service`**: Start the Eureka Server first. It acts as the central registry for all other services.
2.  **`trainer-hours-service`**: Start the secondary microservice. It will register itself with the Eureka Server.
3.  **`gym-crm`**: Start the main microservice. It will register with Eureka and use the Feign client to discover and communicate with `trainer-hours-service`.