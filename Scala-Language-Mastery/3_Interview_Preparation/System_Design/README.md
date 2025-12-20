# 🏗️ System Design & Architecture Patterns

## Design Scalable Systems with Scala

This directory focuses on system design principles, distributed architecture patterns, and scalable solutions using Scala and functional programming paradigms.

---

## 🎯 System Design Foundations

### **Key Principles**
- **Scalability**: Ability to handle growth in users, data, or traffic
- **Reliability**: Fault tolerance, error recovery, and resilience
- **Availability**: System uptime and service guarantees (99.9%, 99.99%, etc.)
- **Efficiency**: Resource utilization and performance optimization
- **Security**: Authentication, authorization, and data protection

### **CAP Theorem Trade-offs**
- **Consistency**: All nodes see same data simultaneously
- **Availability**: Every request receives a response
- **Partition Tolerance**: System continues despite network failures

---

## 🏛️ Scalable Architecture Patterns

### **1. Load Balancing & API Gateway**
```scala
// API Gateway Pattern with functional composition
trait ApiGateway[F[_]] {
  def routeRequest(endpoint: String, method: String, payload: Json): F[Either[Error, Response]]
}

class FunctionalApiGateway[F[_]: MonadError[*[_], Throwable]](
  services: Map[String, Microservice[F]],
  rateLimiter: RateLimiter[F],
  authService: AuthService[F]
) extends ApiGateway[F] {

  def routeRequest(endpoint: String, method: String, payload: Json): F[Either[Error, Response]] = {
    // Functional composition of concerns
    for {
      _ <- rateLimiter.checkLimits(endpoint)
      user <- authService.authenticate(payload)
      authorized <- authService.authorize(user, endpoint, method)
      response <- routeToService(authorized, endpoint, payload)
    } yield Right(response)
  } recoverWith {
    case RateLimitExceeded => Left(TooManyRequestsError)
    case Unauthorized      => Left(AuthError)
    case _                 => Left(InternalServerError)
  }
}
```

### **2. Microservices Communication**
```scala
// Circuit Breaker Pattern for resilient service communication
sealed trait CircuitState
case object Closed extends CircuitState        // Normal operation
case object Open extends CircuitState          // Failure state - reject calls
case object HalfOpen extends CircuitState      // Testing recovery

case class CircuitBreakerConfig(
  failureThreshold: Int = 5,
  recoveryTimeout: FiniteDuration = 60.seconds,
  successThreshold: Int = 3
)

class CircuitBreakerService[F[_]: Concurrent](
  config: CircuitBreakerConfig
)(implicit timer: Timer[F]) {

  private val state = Ref.of[F, CircuitState](Closed).unsafeRunSync()

  def execute[A](serviceCall: F[A]): F[Either[CircuitOpen, A]] = {
    state.get.flatMap {
      case Open => Left(CircuitOpen).pure[F]
      case _    => executeWithFallback(serviceCall)
    }
  }

  private def executeWithFallback[A](serviceCall: F[A]): F[Either[CircuitOpen, A]] = {
    serviceCall.map(Right(_)).handleErrorWith { error =>
      // Record failure and potentially open circuit
      recordFailure *> Left(CircuitOpen).pure[F]
    }
  }
}
```

### **3. Database Design & Caching**
```scala
// Repository Pattern with functional composition
trait UserRepository[F[_]] {
  def findById(id: UserId): F[Option[User]]
  def save(user: User): F[User]
  def findByEmail(email: Email): F[Option[User]]
}

class CachedUserRepository[F[_]: Monad](
  database: UserRepository[F],
  cache: UserCache[F],
  metrics: Metrics[F]
) extends UserRepository[F] {

  def findById(id: UserId): F[Option[User]] = {
    val cacheLookup = cache.get(id)
    val dbLookup = database.findById(id) <* cache.put(id, _)

    // Cache-first strategy with database fallback
    cacheLookup.handleErrorWith(_ => dbLookup)
  }

  def save(user: User): F[User] = {
    for {
      saved <- database.save(user)
      _ <- cache.invalidate(user.id)
      _ <- metrics.increment("user.saved")
    } yield saved
  }
}
```

---

## 📊 Data Architecture Patterns

### **CQRS (Command Query Responsibility Segregation)**
```scala
// Separate read and write models using functional programming
case class BankAccount(id: AccountId, balance: BigDecimal, version: Int)

// Write Model (Commands)
sealed trait AccountCommand
case class Deposit(amount: BigDecimal) extends AccountCommand
case class Withdraw(amount: BigDecimal) extends AccountCommand

// Read Model (Queries)
case class AccountView(balance: BigDecimal, lastUpdated: Instant)

// CQRS with functional composition
class AccountService[F[_]: Monad](
  writeRepository: AccountWriteRepository[F],
  readRepository: AccountReadRepository[F],
  eventStore: EventStore[F]
) {

  def processCommand(accountId: AccountId, command: AccountCommand): F[Either[Error, Unit]] = {
    for {
      events <- generateEvents(accountId, command)
      _ <- eventStore.saveAll(events)
      _ <- updateReadModel(accountId, events)
    } yield Right(())
  }

  def getBalance(accountId: AccountId): F[Option[BigDecimal]] = {
    readRepository.getView(accountId).map(_.map(_.balance))
  }
}
```

### **Event Sourcing Architecture**
```scala
// Event Sourcing with functional streams
sealed trait AccountEvent {
  def accountId: AccountId
  def timestamp: Instant
}
case class Deposited(accountId: AccountId, amount: BigDecimal, timestamp: Instant) extends AccountEvent
case class Withdrawn(accountId: AccountId, amount: BigDecimal, timestamp: Instant) extends AccountEvent

class EventSourcedAccount[F[_]: Monad](
  eventStore: EventStore[F],
  publisher: EventPublisher[F]
) {

  def processEvent(event: AccountEvent): F[AccountEvent] = {
    for {
      validated <- validateEvent(event)
      _ <- eventStore.save(validated)
      _ <- publisher.publish(validated)
    } yield validated
  }

  // Reconstruct state from events (folding)
  def getCurrentState(accountId: AccountId): F[AccountState] = {
    eventStore.getEvents(accountId).map(events =>
      events.foldLeft(AccountState.empty) { (state, event) =>
        event match {
          case Deposited(_, amount, _) => state.copy(balance = state.balance + amount)
          case Withdrawn(_, amount, _) => state.copy(balance = state.balance - amount)
        }
      }
    )
  }
}
```

---

## 🚀 Performance & Scalability

### **Horizontal Scaling Patterns**
```scala
// Distributed worker pattern with Akka-inspired design
trait WorkCoordinator[F[_]] {
  def submitWork(work: WorkItem): F[WorkId]
  def getWorkResult(workId: WorkId): F[Option[WorkResult]]
}

class DistributedCoordinator[F[_]: Concurrent](
  workers: List[Worker[F]],
  loadBalancer: LoadBalancer[F]
) extends WorkCoordinator[F] {

  def submitWork(work: WorkItem): F[WorkId] = {
    for {
      worker <- loadBalancer.selectWorker(work)
      workId <- worker.assignWork(work)
    } yield workId
  }

  def distributeWorkloads(): Stream[F, Unit] = {
    Stream.fixedRate[F](1.second).evalMap { _ =>
      redistributeTasks
    }
  }
}
```

### **Reactive Systems Design**
```scala
// Reactive streams for back-pressure handling
import fs2.Stream

class ReactiveDataProcessor[F[_]: Concurrent] {
  def processStream(input: Stream[F, DataItem]): Stream[F, ProcessedItem] = {
    input
      .evalMap(processWithCircuitBreaker)  // Resilience
      .groupWithin(1000, 1.second)         // Batching
      .evalMap(processBatch)              // Parallel batch processing
      .handleErrorWith(logAndRecover)     // Error recovery
  }

  private def processBatch(batch: Chunk[DataItem]): F[List[ProcessedItem]] = {
    batch.toList.parTraverse(item => validateAndTransform(item))
  }

  private def validateAndTransform(item: DataItem): F[Either[ValidationError, ProcessedItem]] = {
    validate(item).flatMap {
      case Valid(validItem) => transform(validItem).map(Right(_))
      case Invalid(errors)  => Left(ValidationFailed(errors)).pure[F]
    }
  }
}
```

---

## 🔒 Security & Reliability Patterns

### **Authentication & Authorization**
```scala
// Type-safe authentication with functional design
case class UserCredentials(email: Email, passwordHash: PasswordHash)
case class AuthToken(userId: UserId, roles: Set[Role], expiresAt: Instant)

sealed trait AuthResult
case class Authenticated(token: AuthToken) extends AuthResult
case object InvalidCredentials extends AuthResult
case object AccountLocked extends AuthResult

class AuthService[F[_]: Monad](
  userRepository: UserRepository[F],
  tokenService: TokenService[F],
  rateLimiter: RateLimiter[F]
) {

  def authenticate(credentials: UserCredentials): F[AuthResult] = {
    for {
      isAllowed <- rateLimiter.checkRateLimit(credentials.email.value)
      result <- if (isAllowed) authenticateUser(credentials) else AccountLocked.pure[F]
    } yield result
  }

  private def authenticateUser(credentials: UserCredentials): F[AuthResult] = {
    userRepository.findByEmail(credentials.email).flatMap {
      case None => InvalidCredentials.pure[F]
      case Some(user) if checkPassword(user, credentials) =>
        tokenService.generateToken(user).map(Authenticated)
      case _ => InvalidCredentials.pure[F]
    }
  }
}
```

### **Error Handling & Resilience**
```scala
// Comprehensive error handling with ADT
sealed trait ServiceError extends Exception
case class ValidationError(field: String, message: String) extends ServiceError
case class DatabaseError(operation: String, cause: Throwable) extends ServiceError
case class ExternalServiceError(service: String, code: Int) extends ServiceError
case class TimeoutError(operation: String, timeoutMs: Long) extends ServiceError

// Error recovery strategies
class ResilientService[F[_]: MonadError[*[_], ServiceError]: Timer](
  config: ServiceConfig,
  retryPolicy: RetryPolicy[F],
  circuitBreaker: CircuitBreaker[F],
  fallbackService: FallbackService[F]
) {

  def executeWithResilience[A](operation: F[A]): F[A] = {
    circuitBreaker.protect(
      retryPolicy.retry(
        operation.handleErrorWith {
          case ValidationError(_, _) => MonadError[F, ServiceError].raiseError(_)
          case _ => fallbackService.getFallback
        }
      )
    )
  }
}
```

---

## 📈 Monitoring & Observability

### **Metrics Collection**
```scala
// Functional metrics collection
case class ServiceMetrics(
  requestCount: Long,
  errorCount: Long,
  averageResponseTime: Double,
  lastError: Option[String]
)

class MetricsCollector[F[_]: Monad](
  metrics: Ref[F, ServiceMetrics],
  metricsBackend: MetricsBackend[F]
) {

  def recordRequest(duration: FiniteDuration): F[Unit] = {
    metrics.update { current =>
      val newCount = current.requestCount + 1
      val newAvg = (current.averageResponseTime * current.requestCount + duration.toMillis) / newCount
      current.copy(requestCount = newCount, averageResponseTime = newAvg)
    } *>
    metricsBackend.sendGauge("request.duration", duration.toMillis)
  }

  def recordError(error: String): F[Unit] = {
    metrics.update(_.copy(errorCount = _.errorCount + 1, lastError = Some(error))) *>
    metricsBackend.incrementCounter("errors.total", Map("error_type" -> classifyError(error)))
  }

  def getMetrics: F[ServiceMetrics] = metrics.get

  // Health check endpoint
  def healthCheck: F[HealthStatus] = {
    metrics.get.map { currentMetrics =>
      val errorRate = if (currentMetrics.requestCount > 0)
        currentMetrics.errorCount.toDouble / currentMetrics.requestCount
      else 0.0

      if (errorRate < 0.05) Healthy else Unhealthy
    }
  }
}
```

---

## 🧪 Testing Distributed Systems

### **Property-Based Testing for Reliability**
```scala
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen

class SystemDesignProperties extends Properties("SystemDesign") {

  property("idempotent operations maintain state consistency") = forAll {
    (commands: List[BankCommand]) =>
      val system1 = processCommands(commands)
      val system2 = processCommands(commands ++ commands)

      system1.state == system2.state
  }

  property("eventual consistency in distributed system") = forAll {
    (events: List[DomainEvent], networkPartitions: List[NetworkPartition]) =>
      val distributedSystems = simulateNetworkPartitions(events, networkPartitions)

      eventually(distributedSystems.forall(_.state == targetState(events)))
  }

  property("load balancer distributes work evenly") = forAll {
    (requests: List[HttpRequest], workers: List[Worker]) =>
      workers.nonEmpty ==>
        val distribution = simulateLoadBalancing(requests, workers)
        val (minLoad, maxLoad) = (distribution.min, distribution.max)

        maxLoad - minLoad <= 1  // Even distribution
  }
}

def eventually(condition: => Boolean): Boolean = {
  // Implementation of eventual consistency check
  ???
}
```

---

## 🎯 Advanced Design Patterns

### **Saga Pattern for Distributed Transactions**
```scala
// Distributed saga coordination
sealed trait SagaStep
case class CompensableStep(action: () => Future[Unit], compensation: () => Future[Unit]) extends SagaStep
case class PivotStep(action: () => Future[Unit]) extends SagaStep
case class RetriableStep(action: () => Future[Unit], retryPolicy: RetryPolicy) extends SagaStep

class SagaCoordinator(orchestration: List[SagaStep]) {
  def execute(): Future[SagaResult] = {
    orchestration.foldLeft(Future.successful(CompletedSteps.empty)) { (acc, step) =>
      acc.flatMap { completed =>
        executeStep(step).map(_ => completed + step).recoverWith {
          case error => compensate(completed.steps).flatMap(_ => Future.failed(error))
        }
      }
    }.map(_ => SagaSuccess).recover {
      case SagaFailure(rolledBack) => CompensationCompleted(rolledBack)
    }
  }
}
```

### **Hexagonal Architecture**
```scala
// Ports and Adapters pattern in functional style
trait OrderRepository[F[_]] {
  def save(order: Order): F[OrderId]
  def findById(id: OrderId): F[Option[Order]]
}

trait PaymentService[F[_]] {
  def processPayment(amount: BigDecimal, card: CreditCard): F[PaymentResult]
}

trait InventoryService[F[_]] {
  def reserveItems(items: List[StockItem]): F[ReservationId]
}

// Core business logic knows nothing about infrastructure
class OrderService[F[_]: Monad](
  orderRepo: OrderRepository[F],
  paymentService: PaymentService[F],
  inventoryService: InventoryService[F],
  validator: BusinessRules[F]
) {

  def placeOrder(order: Order): F[Either[OrderRejection, OrderConfirmation]] = {
    for {
      validated <- validator.validateOrder(order)
      reservationId <- inventoryService.reserveItems(order.items)
      paymentResult <- paymentService.processPayment(order.total, order.paymentInfo)
      savedOrder <- orderRepo.save(validated.copy(status = Paid, reservationId = Some(reservationId)))
      confirmation = OrderConfirmation(savedOrder.id, paymentResult.transactionId)
    } yield Right(confirmation)
  }
}
```

---

## 📚 Design Process & Decision Making

### **Architecture Decision Records (ADRs)**
```markdown
# ADR 1: Choice of Persistence Layer

## Context
Need to select data storage technology for user session management supporting high concurrency and low latency.

## Decision
Use Redis for session storage over traditional RDBMS.

## Rationale
- **Performance**: In-memory operations provide <1ms response times
- **Scalability**: Horizontal scaling capabilities
- **Data Model**: Key-value structure sufficient for sessions
- **Operational**: Mature tooling and monitoring ecosystem

## Consequences
- **Good**: High performance and horizontal scalability
- **Risk**: Data consistency challenges in distributed deployments
- **Mitigation**: Implement proper caching strategies and fallbacks

## Alternatives Considered
- PostgreSQL: Better consistency but higher latency
- Cassandra: Good scalability but complex queries
- MongoDB: Flexible schema but eventual consistency
```

### **Capacity Planning Framework**
```scala
case class CapacityRequirements(
  dailyActiveUsers: Long,
  readOperationsPerUser: Double,
  writeOperationsPerUser: Double,
  dataRetentionDays: Int,
  availabilityTarget: Double,
  p95LatencyTarget: FiniteDuration
)

case class InfrastructureSizing(
  computeNodes: Int,
  databaseCapacity: Long,  // GB
  cacheMemory: Long,      // GB
  networkBandwidth: Long, // Mbps
  storageGrowthRate: Double
)

class CapacityPlanner {

  def planCapacity(requirements: CapacityRequirements): InfrastructureSizing = {
    val totalReadOps = requirements.dailyActiveUsers * requirements.readOperationsPerUser
    val totalWriteOps = requirements.dailyActiveUsers * requirements.writeOperationsPerUser

    InfrastructureSizing(
      computeNodes = estimateComputeNodes(totalReadOps, totalWriteOps),
      databaseCapacity = estimateStorage(requirements) + safetyMargin,
      cacheMemory = estimateCacheRequirements(totalReadOps),
      networkBandwidth = estimateNetworkBandwidth(totalReadOps, totalWriteOps),
      storageGrowthRate = estimateGrowthRate(requirements.dataRetentionDays)
    )
  }
}
```

---

*"Great system design combines technical excellence with business acumen, scalability with maintainability, and innovation with practicality."*

**Master these patterns, and you'll architect systems that can grow from startup to enterprise while maintaining reliability and developer happiness.**
