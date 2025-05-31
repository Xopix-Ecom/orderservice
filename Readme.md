# 🚀 Order Service - E-commerce Backend

Welcome to the **Order Service**, a microservice in our modular e-commerce platform. This service handles orders placements, product data retrieval, and user association, all with resilience and caching built-in.

---

## 📦 Features

* 🛒 Place orders with dynamic product data
* 🔄 Communicate with **Product Service** using Feign Client
* 🔐 Associate orders with **User IDs**
* 🚦 Resilience via **Resilience4j** (circuit breaker, fallback, retry)
* ⚡ Redis-based caching for product info (Docker-powered)
* 🗃️ Store orders in **MySQL**
* 🛠️ DTO mapping using MapStruct

---

## 🧰 Tech Stack

* Java 20+ Spring Boot
* Spring Data JPA + MySQL
* OpenFeign (Product Service Communication)
* Resilience4j
* Redis (Docker)
* MapStruct

---

## 🔗 API Endpoints

### ✅ Create Order

```
POST /api/orders/create
```

**Request Params:**

* `productId`: Long
* `quantity`: int
* `userId`: Long

**Sample Response:**

```json
{
  "orderId": 1,
  "productId": 100,
  "quantity": 2,
  "product": {
    "productId": 100,
    "name": "Sample Product",
    "description": "Product Description",
    "price": 25.99,
    "imageUrl": "http://example.com/image.jpg"
  }
}
```

---

## 🚀 Getting Started

### 🧾 Prerequisites

* Java 17+
* Maven
* MySQL (local): `localhost:3306`, DB: `orders_db`
* Docker (for Redis)

### 🐳 Run Redis using Docker

```bash
docker run -d --name redis-orders-service -p 6379:6379 redis
```

### 🛢️ Setup MySQL Database

Create the database manually or via script:

```sql
CREATE DATABASE orders_db;
```

Update DB credentials in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/orders_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 🏗️ Build and Run the Service

```bash
mvn clean install
java -jar target/orders-service-0.0.1-SNAPSHOT.jar
```

### 🧪 Verify Product Service

Make sure **Product Service** is reachable:

```properties
product.service.url=http://localhost:8081
```

---

## 🧠 Redis Caching Behavior

* If **Product Service** is up: product info is fetched and cached.
* If **Product Service** is down: cached info is served (if exists).

---

## 🔐 Circuit Breaker & Retry

* Configured with **Resilience4j**
* Fallback method ensures reliability
* All properties in `application.properties`

---

## 📝 Notes

* MySQL stores orders data along with userId
* User Service is expected to run separately
* You can add API gateway or auth layer as needed

---

## 🔮 Future Enhancements

* 📖 Add Swagger UI for API docs
* 👤 Integrate User Service for enriched responses
* 🔒 Add authentication and authorization
