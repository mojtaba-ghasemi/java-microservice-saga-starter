# Java Microservice Saga Starter

A minimal but complete implementation of Saga Pattern for distributed transactions in microservices architecture using Spring Boot, RabbitMQ, MongoDB, and Orchestrator-based Saga.

## Repository Structure

```
java-microservice-saga-starter/
├── iam-service/                    # User management microservice
├── iam-service-model/              # Shared IAM models (DTOs, Events)
├── saga-orchestrator-service/      # Main Saga coordinator
├── shared-model/                   # Common models across services
└── script                          # MongoDB Role Collection Script
```

## Quick Start with Docker Compose

```bash
git clone https://github.com/m-master22/java-microservice-saga-starter.git
cd java-microservice-saga-starter
mvn clean install
```

## Service URLs

| Service | URL |
|---------|-----|
| Saga Orchestrator Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Saga Orchestrator OpenAPI | http://localhost:8080/v3/api-docs |
| IAM Service Swagger UI | http://localhost:8081/swagger-ui/index.html |
| IAM Service OpenAPI | http://localhost:8081/v3/api-docs |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| MongoDB IAM | localhost:27017 |
| MongoDB Orchestrator | localhost:27018 |

## API Endpoints

### IAM Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/backoffice/iam/auth/signup` | Register a new admin |
| POST | `/backoffice/iam/auth/login` | Authenticate user |
| POST | `/customer/definition/auth/signup-customer` | Customer signup |

### Request/Response Schemas

**SignUpRequestDto**
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "baseUsername": "string"
}
```

**LoginRequestDto**
```json
{
  "username": "string",
  "password": "string"
}
```

**LoginResponseDto**
```json
{
  "roles": [
    {
      "id": "string",
      "title": "string",
      "claims": ["ALL"]
    }
  ],
  "acceptTermsAndConditions": true
}
```

## Test Commands

### Signup via IAM Service
```bash
curl -X POST http://localhost:8081/backoffice/iam/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"johnDoe","password":"SecurePass123","email":"john@example.com"}'
```

### Login via IAM Service
```bash
curl -X POST http://localhost:8081/backoffice/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johnDoe","password":"SecurePass123"}'
```

### User Registration via Saga Orchestrator
```bash
curl -X POST http://localhost:8080/api/saga/register \
  -H "Content-Type: application/json" \
  -d '{"username":"johnDoe","email":"john@example.com","password":"SecurePass123"}'
```

## Event Flow in RabbitMQ

| Component | Exchange | Queue | Routing Key |
|-----------|----------|-------|-------------|
| Orchestrator → IAM | saga.exchange | iam.command.queue | command.user.create |
| IAM → Orchestrator | saga.exchange | orchestrator.event.queue | event.user.created |
| IAM → Orchestrator | saga.exchange | orchestrator.event.queue | event.user.creation-failed |

## Database Structure

### MongoDB Databases
- **identities**: Stores users collection with unique index on username
- **orchestrator**: Stores saga_log collection for tracking sagas

```

## Manual Setup (Without Docker)

```bash

# Run services (in separate terminals)
cd iam-service && mvn spring-boot:run
cd saga-orchestrator-service && mvn spring-boot:run
```

## Monitoring Commands

```bash
# Check MongoDB data
docker exec -it mongo-iam mongosh iam_db
db.users.find().pretty()

# Check RabbitMQ queues
docker exec -it rabbitmq rabbitmqctl list_queues

# View service logs
docker-compose logs iam-service
docker-compose logs saga-orchestrator-service
```

## Technology Stack

- Spring Boot 3.5
- Spring Cloud Stream RabbitMQ
- MongoDB 7+
- RabbitMQ
- Java 21
- Maven 3.9+
- Lombok
- Swagger/OpenAPI 3.0

## Current Features

- RabbitMQ integration (event production & consumption)
- MongoDB persistence with indexes
- Orchestrator-based Saga coordination
- Complete user registration flow
- Error handling & failure events
- Docker Compose for one-command setup
- Database initialization scripts
- Swagger UI for API documentation

## Missing Features (Planned)


## License

MIT License
```
