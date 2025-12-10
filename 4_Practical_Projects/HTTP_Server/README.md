# 🛠️ REST API Server with Scala & HTTP4s

## Production-Ready Web API in Functional Scala

**Phase 4: Advanced Practical Projects (HTTP Server)**

**Prerequisites**: Complete all Beginner & Intermediate phases

**Duration**: 4-6 weeks to build and understand

---

## 🎯 What You'll Learn

- **Functional Web Development** with HTTP4s
- **Type-Safe Configuration** using PureConfig
- **JSON Handling** with Circe on Scala
- **Functional Error Handling** using Either & Cats Effect
- **RESTful API Design** following industry standards
- **Middleware Implementation** (CORS, logging, rate limiting)
- **Dependency Injection** with functional composition
- **Production-Ready Patterns** for scalable APIs

---

## 🏗️ Architecture Overview

```
REST API Server Architecture
├── 🔧 Configuration Layer          (application.conf + PureConfig)
│   ├── Server Settings            (host, port, timeouts)
│   ├── Database Configuration     (connection settings)
│   ├── Security Policies         (CORS, rate limiting)
│   └── Logging Configuration     (levels, destinations)
│
├── 📊 Domain Layer                (Case classes & enumerations)
│   ├── User & Task Models        (ALG, Email, UserId types)
│   ├── Business Entities         (aggregates with business logic)
│   ├── Value Objects            (Email, UserId, TaskId)
│   └── Status Enumerations       (UserStatus, TaskStatus)
│
├── 🏪 Repository Layer            (Data access abstractions)
│   ├── UserRepository[F[_]]      (CRUD operations interface)
│   ├── TaskRepository[F[_]]      (CRUD operations interface)
│   └── InMemory*Repository[F]    (In-memory implementations)
│
├── 💼 Service Layer              (Business logic composition)
│   ├── UserService[F[_]]         (User domain operations)
│   ├── TaskService[F[_]]         (Task domain operations)
│   └── *ServiceImpl[F]          (Concrete service implementations)
│
├── 🌐 HTTP Layer                 (Web interface & routing)
│   ├── UserRoutes[F[_]: Sync]    (User API endpoints)
│   ├── TaskRoutes[F[_]: Sync]    (Task API endpoints)
│   └── Middleware Stack          (CORS, logging, rate limiting)
│
└── 🚀 Main Application           (Composition & startup)
    ├── ApiServer extends IOApp  (Entry point with Cats Effect)
    ├── Configuration Loading    (PureConfig integration)
    └── Server Lifecycle        (Startup, graceful shutdown)
```

---

## 🚀 Getting Started

### **Prerequisites**
- Scala 2.13.8+
- Java 11+
- sbt 1.5+

### **Installation & Setup**

1. **Navigate to the project directory:**
   ```bash
   cd 4_Practical_Projects/HTTP_Server
   ```

2. **Download dependencies:**
   ```bash
   sbt update
   ```

3. **Compile the project:**
   ```bash
   sbt compile
   ```

4. **Run the server:**
   ```bash
   sbt run
   ```

You should see output like:
```
Starting Scala REST API Server v1.0.0
Server starting on http://0.0.0.0:8080
Health check available at: http://localhost:8080/health
🛠️  REST API SERVER - AVAILABLE ENDPOINTS
🌐 Server: http://localhost:8080
📊 Health: GET /health
```

---

## 🧪 Testing the API

### **Health Check**
```bash
curl http://localhost:8080/health
# Expected: {"success":true,"message":"API is healthy"}
```

### **Create Users**
```bash
# Create Alice
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","name":"Alice Johnson"}'

# Create Bob
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","name":"Bob Smith"}'

# Expected: HTTP 201 with user details
```

### **List Users**
```bash
curl -X GET "http://localhost:8080/api/v1/users?limit=10&offset=0"
# Expected: JSON with users array and pagination info
```

### **Create Tasks**
```bash
# Create a task assigned to Alice
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication with refresh tokens",
    "priority": "high",
    "assigneeEmail": "alice@example.com"
  }'

# Create another task
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Write unit tests",
    "description": "Add comprehensive unit tests for authentication module",
    "priority": "medium",
    "assigneeEmail": "bob@example.com"
  }'
```

### **List Tasks**
```bash
# List all tasks
curl -X GET "http://localhost:8080/api/v1/tasks"

# List tasks assigned to Alice
curl -X GET "http://localhost:8080/api/v1/tasks?assigneeEmail=alice@example.com"
```

### **Update Tasks**
```bash
# Update task status (replace TASK_ID from previous response)
curl -X PUT http://localhost:8080/api/v1/tasks/TASK_ID \
  -H "Content-Type: application/json" \
  -d '{"status": "in_progress"}'

# Update task priority and assignee
curl -X PUT http://localhost:8080/api/v1/tasks/TASK_ID \
  -H "Content-Type: application/json" \
  -d '{"priority": "urgent"}'

# Assign task to different user
curl -X PUT http://localhost:8080/api/v1/tasks/TASK_ID/assign \
  -H "Content-Type: application/json" \
  -d '{"assigneeEmail": "bob@example.com"}'
```

---

## 📋 API Specification

### **HTTP Status Codes**
- **200 OK** - Success for GET, PUT, DELETE
- **201 Created** - Resource created successfully
- **204 No Content** - Operation successful, no response body
- **400 Bad Request** - Validation errors or malformed request
- **404 Not Found** - Resource not found
- **409 Conflict** - Resource conflict (like duplicate email)
- **500 Internal Server Error** - Server-side errors

### **Response Formats**

#### **Success Response**
```json
{
  "data": { /* Object or array */ },
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 45,
    "totalPages": 3
  }
}
```

#### **Error Response**
```json
{
  "error": "ValidationError",
  "message": "Title cannot be empty",
  "timestamp": "2023-12-10T17:30:00Z"
}
```

#### **Pagination**
```json
{
  "page": 1,
  "limit": 20,
  "total": 45,
  "totalPages": 3
}
```

---

## 🔧 Configuration

### **Application Settings**
The server is configured through `src/main/resources/application.conf`:

```hocon
api {
  host = "0.0.0.0"           # Server bind address
  port = 8080               # Server port
  shutdownTimeout = 30 seconds  # Graceful shutdown timeout
}

app {
  security.cors {
    allowOrigin = ["*"]     # CORS settings
    allowMethods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allowHeaders = ["Content-Type", "Authorization"]
  }

  security.rateLimit {
    requestsPerMinute = 60  # Rate limiting
    burstLimit = 20
  }
}
```

### **Runtime Configuration**
Modify any setting and restart the server:
```bash
# Change port to 3000
# Edit application.conf: port = 3000
# Restart server: sbt run
# Server will start on port 3000
```

---

## 🔄 Request-Response Flow

### **1. Request Processing**
```
HTTP Request → Middleware (CORS, Rate Limiting, Logging) →
Route Matching → Request Decoding → Validation →
Business Logic → Repository → Response Encoding →
JSON Response
```

### **2. Error Handling**
```scala
// All errors are handled functionally
sealed trait AppError
case class ValidationError(errors: List[String]) extends AppError
case class NotFoundError(resource: String) extends AppError
case class ConflictError(message: String) extends AppError

// Handled with Either and Cats Effect
def processRequest(request: CreateUserRequest): F[Either[AppError, User]] = {
  request.validate.toEither match {
    case Right(validReq) => userService.createUser(validReq)
    case Left(errors) => ValidationError(errors).asLeft.pure[F]
  }
}
```

### **3. Middleware Stack**
```scala
// Applied in this order:
// 1. Request Logging
// 2. CORS Headers
// 3. Rate Limiting
// 4. Route Handler
// 5. Response Formatting
val middlewareStack = requestLogging(cors(rateLimiting(routes)))
```

---

## 🧪 Testing the Server

### **Manual Testing with curl**
```bash
# Full workflow test script
#!/bin/bash

# Start testing sequence
echo "Starting API test sequence..."

# 1. Health check
echo "1. Testing health endpoint..."
curl -s http://localhost:8080/health

# 2. Create users
echo "2. Creating test users..."
curl -s -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test User"}'

# 3. Create tasks
echo "3. Creating test tasks..."
curl -s -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","description":"Testing the API","priority":"low"}'

# 4. List resources
echo "4. Listing resources..."
curl -s "http://localhost:8080/api/v1/users?limit=5"
echo ""
curl -s "http://localhost:8080/api/v1/tasks?limit=5"
```

### **Automated Testing with ScalaTest**
```scala
// In src/test/scala/restapiserver/
import org.http4s.testing.Http4sMatchers
import org.scalatest.flatspec.AnyFlatSpec
import cats.effect.testing.scalatest.AsyncIOSpec

class ApiSpec extends AnyFlatSpec with AsyncIOSpec with Http4sMatchers {

  "User API" should "create and retrieve user" in {
    val createRequest = createUserRequest("test@example.com", "Test User")
    val routes = createTestRoutes()

    for {
      createResponse <- routes.run(createUserRequest).unsafeToFuture()
      _ = createResponse.status shouldBe Status.Created

      // Extract user ID and test GET request
      createdUser = parse[UserResponse](createResponse.bodyString)
      getRequest = Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${createdUser.id}"))
      getResponse <- routes.run(getRequest).unsafeToFuture()
      retrievedUser = parse[UserResponse](getResponse.bodyString)
    } yield {
      getResponse.status shouldBe Status.Ok
      retrievedUser.email shouldBe "test@example.com"
      retrievedUser.name shouldBe "Test User"
    }
  }

  "Task API" should "handle task lifecycle" in {
    // Similar test patterns for tasks
  }
}
```

---

## 🚀 Extending the Server

### **Adding New Endpoints**
```scala
// 1. Add to domain models
case class Comment(id: CommentId, taskId: TaskId, authorId: UserId, content: String)

// 2. Add repository interface
trait CommentRepository[F[_]] {
  def save(comment: Comment): F[Comment]
  def findByTask(taskId: TaskId): F[List[Comment]]
}

// 3. Add service layer
class CommentService[F[_]: Sync](commentRepo: CommentRepository[F]) {
  def addComment(taskId: TaskId, authorId: UserId, content: String): F[Either[String, Comment]] = {
    if (content.trim.isEmpty) "Comment cannot be empty".asLeft.pure[F]
    else Comment(CommentId(UUID.randomUUID()), taskId, authorId, content).asRight.pure[F]
  }
}

// 4. Add HTTP routes
class CommentRoutes[F[_]: Sync](commentService: CommentService[F]) extends Http4sDsl[F] {
  val routes: HttpRoutes[F] = HttpRoutes.of {
    case req @ POST -> Root / "tasks" / UUIDVar(taskId) / "comments" =>
      // Implementation...
      Created(???)  // Return created comment
  }
}

// 5. Add to server composition
val commentService = new CommentService[IO](commentRepo)
val commentRoutes = new CommentRoutes[IO](commentService)
val allRoutes = userRoutes <+> taskRoutes <+> commentRoutes
```

### **Changing Database Backend**
```scala
// Replace InMemory with Doobie for PostgreSQL
libraryDependencies += "org.tpolecat" %% "doobie-postgres" % "1.0.0"

// Repository implementation using Doobie
class PostgresUserRepository[F[_]: Async](xa: Transactor[F])
    extends UserRepository[F] {

  private val findByIdQ = sql"SELECT id, email, name, status, created_at FROM users WHERE id = $uuid"
    .query[User].option

  override def findById(id: UserId): F[Option[User]] =
    findByIdQ(id.value).transact(xa)
}

// Configuration update (in application.conf)
database {
  driver = "org.postgresql.Driver"
  url = "jdbc:postgresql://localhost:5432/myapp"
  username = "myapp"
  password = "secret123"
}
```

### **Adding Authentication**
```scala
// JWT integration
libraryDependencies += "dev.profunktor" %% "jwt-http4s" % "1.8.1"

// Auth middleware
class AuthMiddleware[F[_]: Sync](key: String) {
  def authenticate(token: String): F[Either[String, UserId]] = {
    // JWT verification logic
    Try {
      Jwt.decode(token, key, Seq(JwtAlgorithm.HS256))
        .claims.getAs[UUID]("userId")
    } match {
      case Success(Some(userId)) => UserId(userId).asRight.pure[F]
      case _ => "Invalid token".asLeft.pure[F]
    }
  }

  def middleware(routes: HttpRoutes[F]): HttpRoutes[F] = HttpRoutes.of {
    case req @ _ =>
      req.headers.get(ci"Authorization") match {
        case Some(Authorization(Credentials.Token(AuthScheme.Bearer, token))) =>
          authenticate(token.toString).flatMap {
            case Right(userId) =>
              // Add user to request context and proceed
              middlewareContext(routes, userId).run(req)
            case Left(error) =>
              Forbidden(ErrorResponse("Authentication failed", error))
          }
        case None =>
          routes.run(req.updateUri(_.path / "public"))
      }
  }
}
```

---

## 🎯 Learning Outcomes & Next Steps

### **Functional Programming Mastery**
- **Type Classes**: Config, JSON codecs, service patterns
- **Monadic Composition**: HTTP4s routes, Cats Effect programs
- **Functional Error Handling**: Either-based pipeline errors
- **Immutable Architecture**: Pure functions with controlled side effects

### **Web Development Skills**
- **RESTful API Design**: HTTP methods, status codes, resource modeling
- **Middleware Architecture**: CORS, logging, rate limiting patterns
- **Request/Response Cycle**: JSON serialization, validation, transformation
- **Server Lifecycle**: Configuration, startup, graceful shutdown

### **Production-Ready Patterns**
- **Dependency Injection**: Functional composition of services
- **Configuration Management**: Type-safe external configuration
- **Logging & Monitoring**: Structured logging with contextual information
- **Error Bound Bridges**: Robust error handling across layers

### **Expand Your Knowledge**
- **Real Database**: Replace in-memory with PostgreSQL using Doobie
- **Authentication**: Add JWT-based authentication system
- **WebSockets**: Add real-time features with HTTP4s WebSocket support
- **Microservices**: Split into separate user and task services
- **Containerization**: Add Docker configuration
- **Deployment**: Add CI/CD with automated testing

---

## 📚 Resources for Deepening Knowledge

### **HTTP4s Documentation**
- [Official HTTP4s Guide](https://http4s.org/)
- [Type-Level Payroll Case Study](https://github.com/http4s/http4s/tree/main/examples)
- [Cats Effect Patterns](https://typelevel.org/cats-effect/)

### **Circe JSON Library**
- [Circe User Guide](https://circe.github.io/circe/)
- [JSON Schema Design](https://json-schema.org/learn/)
- [Functional JSON Processing](https://github.com/circe/circe/tree/master/modules/docs/src/main/tut)

### **Production Scala Web Apps**
- [Play Framework](https://www.playframework.com/) - Popular alternative
- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/) - Actor-based
- [Finch](https://finagle.github.io/finch/) - Compositional approach

---

*"The hardest part of building a web application is not the technical implementation, but designing the right abstractions and error-handling patterns. This project demonstrates enterprise-grade Scala web development practices that scale."*

**Next**: Deploy this API to production, add authentication, or extend it with a frontend! 🚀
