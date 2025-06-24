# LHV customer api test task

A Spring Boot REST API for managing customer data with full CRUD operations, built with Java 21 and modern enterprise patterns.

## Prerequisites

- Java 21 or higher
- Gradle 8.x (or use included wrapper)

## Customer Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/customers` | Get all customers |
| `GET` | `/customers/{id}` | Get customer by ID |
| `POST` | `/customers` | Create new customer |
| `PUT` | `/customers/{id}` | Update existing customer |
| `DELETE` | `/customers/{id}` | Delete customer |

## Swagger

* Api docs
    * http://localhost:8080/v3/api-docs
* UI
  * http://localhost:8080/swagger-ui/index.html#/