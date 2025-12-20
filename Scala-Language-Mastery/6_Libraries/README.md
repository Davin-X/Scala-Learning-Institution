# 📚 Scala Libraries & Frameworks

## Phase 7: Advanced Ecosystem & Professional Development

**Prerequisites**: Complete all previous phases

**Duration**: 4-6 weeks

---

## 🎯 Learning Goals

- Master functional programming with Cats and Scalaz
- Build robust applications with error handling libraries
- Create type-safe configurations with PureConfig
- Implement reactive workflows with FS2
- Add testing capabilities with ScalaTest and Property-Based Testing
- Understand build tool ecosystems and dependency management

---

## 📋 Library Categories

### **7A: Functional Programming Libraries**
- **Cats**: Essential functional programming abstractions
- **Scalaz**: Alternative FP library with different trade-offs
- **Cats-Effect**: Pure functional effect system
- **Cats-Retry**: Retry patterns for resilience

### **7B: Configuration & Serialization**
- **PureConfig**: Type-safe configuration loading
- **Circe**: JSON processing with type safety
- **Scala-Xml**: XML processing for enterprise systems
- **Config**: HOCON format configuration

### **7C: Testing & Quality Assurance**
- **ScalaTest**: Flexible testing framework
- **ScalaCheck**: Property-based testing
- **ScalaMock**: Mocking library for isolation testing
- **Specs2**: BDD-style testing framework

### **7D: Utilities & Productivity**
- **Better Files**: Simplified file operations
- **Scala Logging**: Professional logging interface
- **Sourcecode**: Macro-based debugging utilities
- **Shapeless**: Generic programming toolkit

### **7E: Reactive & Streaming**
- **FS2**: Functional streaming library
- **Monix**: Reactive programming for JVM
- **ZIO**: Effect system and error handling
- **Http4s**: Purely functional HTTP library

### **7F: Enterprise & Database**
- **Doobie**: Purely functional JDBC layer
- **Skunk**: PostgreSQL driver for cats-effect
- **Quill**: Compile-time validated SQL queries
- **Slick**: Modern database query and access library

---

## 🔧 Deep Dive: Cats Functional Programming

### **Type Classes in Cats**
```scala
import cats._
import cats.implicits._
import cats.data._

// Semigroup - combining values
val intList = List(1, 2, 3, 4, 5)
val combined = intList.combineAll  // 15

val stringList = List("a", "b", "c")
stringList.combineAll  // "abc"

// Monoid - semigroup with identity element
Monoid[Int].empty      // 0
Monoid[String].empty   // ""
Monoid[List[Int]].empty // List()

// Functor - mapping over contexts
val list = List(1, 2, 3)
val doubled = list.map(_ * 2)  // Functor operation
val optional = Some(42)
optional.map(_ * 2)  // Some(84)
```

### **Advanced Cats Patterns**
```scala
import cats.effect._
import cats.effect.concurrent.Ref

// Reader monad for dependency injection
case class Config(url: String, port: Int)
case class Dependencies(config: Config, client: HttpClient)

def databaseCall[F[_]: Sync](dependencies: Dependencies): F[Int] =
  ???  // Uses dependencies.config and dependencies.client

// Using Reader
type AppEnvironment[F[_]] = Reader[F, Dependencies, ?]
def databaseCallWithReader[F[_]]: AppEnvironment[F] = ???  // Pure dependency injection

// State monad for mutable state in pure functional style
case class GameState(score: Int, position: (Int, Int))

def moveUp: State[GameState, Unit] = State.modify { state =>
  state.copy(position = (state.position._1, state.position._2 + 1))
}

def addScore(points: Int): State[GameState, Unit] = State.modify { state =>
  state.copy(score = state.score + points)
}

val gameFlow = for {
  _ <- moveUp
  _ <- addScore(10)
  _ <- moveUp
} yield ()

// Run the state computation
val initialState = GameState(0, (0, 0))
val (finalState, _) = gameFlow.run(initialState).value
println(finalState)  // GameState(10, (0, 2))
```

### **Cats Effect - Pure Functional Effects**
```scala
import cats.effect.{IO, ExitCode}
import cats.implicits._

object CatsEffectExample extends IOApp {

  // Pure functions that return effects
  def readUserInput: IO[String] = IO(scala.io.StdIn.readLine())

  def writeOutput(message: String): IO[Unit] = IO(println(message))

  def validateInput(input: String): Either[String, Int] =
    input.toIntOption.toRight("Invalid number")

  // Composable error handling with Eithers
  def processInput(input: String): IO[String] = {
    IO.fromEither(validateInput(input)).map { number =>
      s"Processed number: ${number * 2}"
    }.handleErrorWith { error =>
      IO(s"Error processing input: $error")
    }
  }

  // Resource management with bracket
  def withDatabaseConnection[A](f: String => IO[A]): IO[A] = {
    val acquire = IO(println("Connecting to database..."))
    val release = (_: Unit) => IO(println("Closing connection..."))

    Resource.make(acquire)(_ => release).use(_ =>
      f("database connection here")
    )
  }

  // Main application flow using cats
  def program: IO[Unit] = for {
    _ <- writeOutput("Please enter a number:")
    input <- readUserInput
    result <- processInput(input)
    _ <- writeOutput(result)

    // Demonstrate concurrency
    numbers <- List(1, 2, 3, 4).parTraverse(n =>
      IO(s"Processing $n").delayBy(100.millis)
    )
    _ <- writeOutput(s"Parallel results: $numbers")

  } yield ()

  def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success).handleErrorWith { error =>
      IO(println(s"Fatal error: $error")).as(ExitCode.Error)
    }
}
```

---

## 🔧 PureConfig: Type-Safe Configuration

```scala
import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.duration._

// Define configuration case classes
case class HttpConfig(
  host: String,
  port: Int,
  timeout: FiniteDuration
)

case class DatabaseConfig(
  url: String,
  username: String,
  password: String,
  poolSize: Int,
  retryConfig: RetryConfig
)

case class RetryConfig(
  maxAttempts: Int,
  backoffFactor: Double,
  initialDelay: FiniteDuration
)

case class AppConfig(
  http: HttpConfig,
  database: DatabaseConfig,
  features: Map[String, Boolean]
)

// Load configuration from HOCON file
val config = ConfigSource.file("application.conf").load[AppConfig]

// Using the configuration
config match {
  case Right(appConfig) =>
    println(s"HTTP port: ${appConfig.http.port}")
    println(s"Database URL: ${appConfig.database.url}")
    println(s"Feature flags: ${appConfig.features}")

  case Left(errors) =>
    errors.toList.foreach(error =>
      println(s"Config error: $error")
    )
}

// application.conf
/*
app {
  http {
    host = "localhost"
    port = 8080
    timeout = 30 seconds
  }
  database {
    url = "jdbc:postgresql://localhost:5432/mydb"
    username = "postgres"
    password = "secret"
    pool-size = 10
    retry-config {
      max-attempts = 3
      backoff-factor = 2.0
      initial-delay = 1 second
    }
  }
  features {
    enable-cache = true
    enable-metrics = false
    debug-mode = true
  }
}
*/
```

---

## 🧪 Advanced Testing with ScalaCheck

```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Prop.forAll
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class AdvancedTestingExamples extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {

  // Property-based testing with ScalaCheck
  "List.reverse" should "be its own inverse" in {
    forAll { (list: List[Int]) =>
      list.reverse.reverse should equal (list)
    }
  }

  it should "preserve the length of the list" in {
    forAll { (list: List[String]) =>
      whenever(list.length >= 0) {
        list.reverse.length should equal (list.length)
      }
    }
  }

  // Custom generators for domain-specific testing
  import org.scalacheck.Gen
  import org.scalacheck.Arbitrary

  case class Person(name: String, age: Int)
  val genPerson = for {
    name <- Gen.alphaStr.filter(_.nonEmpty)
    age <- Gen.choose(0, 120)
  } yield Person(name, age)

  implicit val arbPerson: Arbitrary[Person] = Arbitrary(genPerson)

  "Person validation" should "accept valid persons" in {
    forAll { (person: Person) =>
      whenever(person.name.nonEmpty && person.age >= 0 && person.age <= 120) {
        // Test your validation logic
        person.name.length should be > 0
        person.age should (be >= 0 and be <= 120)
      }
    }
  }

  // State-based testing (model what the system should do)
  import org.scalatest.GivenWhenThen

  "Shopping cart" should "maintain correct totals" in {
    Given("an empty shopping cart")
    val cart = ShoppingCart.empty

    When("items are added")
    val cartWithItems = cart.addItem(Item("book", 15.99)).addItem(Item("pen", 2.50))

    Then("total should be calculated correctly")
    cartWithItems.total should equal (18.49)
  }

  // Mock testing with ScalaMock
  import org.scalamock.scalatest.MockFactory

  "User service" should "verify password correctly" ignore with MockFactory {
    val mockUserRepo = mock[UserRepository]
    val userService = new UserService(mockUserRepo)

    val user = User("john", "hashed_password")
    val passwordAttempt = "secret"

    // Setup expectations
    (mockUserRepo.findByUsername _).expects("john").returning(Some(user))
    // (mockPasswordHasher.check _).expects(passwordAttempt, user.hashedPassword).returning(true)

    // Execute and verify
    val result = userService.authenticate("john", passwordAttempt)
    result should be (true)
  }
}

// Example domain objects for testing
case class Item(name: String, price: Double)
case class ShoppingCart(items: List[Item]) {
  def addItem(item: Item): ShoppingCart = copy(items = item :: items)
  def total: Double = items.map(_.price).sum
}

object ShoppingCart {
  def empty: ShoppingCart = ShoppingCart(Nil)
}

trait UserRepository {
  def findByUsername(username: String): Option[User]
}

case class User(username: String, hashedPassword: String)

class UserService(userRepo: UserRepository) {
  def authenticate(username: String, password: String): Boolean = {
    // Implementation would use password hasher
    userRepo.findByUsername(username).isDefined
  }
}
```

---

## 🔄 FS2: Functional Streaming

```scala
import cats.effect._
import fs2._
import scala.concurrent.duration._

object StreamingExample extends IOApp {

  // Basic stream creation and operations
  val numberStream: Stream[IO, Int] = Stream.emits(List(1, 2, 3, 4, 5))

  val processedStream = numberStream
    .filter(_ % 2 == 0)      // Keep even numbers
    .map(_ * 2)             // Double them
    .take(3)                // Take first 3
    .compile              // Compile to effect
    .toList               // Collect to list

  // Resource management with streams
  def readFile(path: String): Resource[IO, Stream[IO, Byte]] = {
    Resource.make(
      openFile(path)  // Acquire
    )(closeFile)      // Release
  }

  // Composable error handling
  def processWithRetry[R](streamOp: Stream[IO, R]): Stream[IO, Either[Throwable, R]] = {
    streamOp.attempt  // Convert errors to Either
                    .collect { case Right(value) => Right(value) }
  }

  // Concurrent processing
  val parallelProcessing = Stream.emits(1 to 10)
    .mapAsync(4)(processNumber)  // Process up to 4 concurrently
    .compile
    .toVector

  def processNumber(num: Int): IO[String] = {
    IO.sleep(100.millis) *> IO(s"Processed: $num")
  }

  // Real-time filtering and aggregation
  def processSensorData(sensorData: Stream[IO, SensorReading]): Stream[IO, AggregatedData] = {
    sensorData
      .groupWithin(100, 1.second)  // Group 100 elements or 1 second
      .map { chunk =>
        val readings = chunk.toList
        val avgTemperature = readings.map(_.temperature).sum / readings.size
        val maxTemperature = readings.map(_.temperature).max
        AggregatedData(avgTemperature, maxTemperature, readings.size)
      }
  }

  // Infinite streams for continuous processing
  val infiniteNumbers = Stream.iterate(0)(_ + 1)
  val throttledStream = infiniteNumbers
    .metered(1.second)           // Emit one element per second
    .take(10)                    // Stop after 10 elements
    .map(n => s"Tick: $n")       // Format output

  // Main application
  def run(args: List[String]): IO[ExitCode] = {

    val program = for {
      // Basic processing
      numbers <- processedStream
      _ <- IO(println(s"Even numbers doubled: $numbers"))

      // Parallel processing
      parallelResults <- parallelProcessing
      _ <- IO(println(s"Parallel results: $parallelResults"))

      // Stream sensor data simulation
      sensorReadings = Stream.iterateEval(SensorReading(20.0, 0L)) { reading =>
        IO.sleep(500.millis) *> IO {
          SensorReading(
            temperature = reading.temperature + (scala.util.Random.nextDouble() - 0.5),
            timestamp = reading.timestamp + 500
          )
        }
      }.take(20)

      // Process sensor data
      aggregatedResults <- processSensorData(sensorReadings).compile.toList
      _ <- IO(println(s"Sensor data aggregates: ${aggregatedResults.take(3)}"))

    } yield ()

    program.as(ExitCode.Success)
  }
}

case class SensorReading(temperature: Double, timestamp: Long)
case class AggregatedData(avgTemp: Double, maxTemp: Double, count: Int)

// Placeholder functions for example
def openFile(path: String): IO[Stream[IO, Byte]] = ???
def closeFile(stream: Stream[IO, Byte]): IO[Unit] = ???
```

---

## 📁 Library Project Structure

```
7_Libraries/
├── README.md                           # This overview
├── cats_examples/                     # Cats functional programming
├── testing_with_scalatest/            # Testing frameworks
├── configuration_pureconfig/          # Configuration management
├── streaming_fs2/                     # Functional streaming
├── database_doobie/                   # Database access patterns
├── http_http4s/                       # HTTP services
├── json_circe/                        # JSON processing
├── error_handling_zio/                # Advanced error handling
├── build_examples/                    # Multi-module build setup
└── microservice_template/             # Complete application template
```

---

## 🧪 Hands-On Library Integration Projects

### **Project 1: Functional Task Manager**
- Build task management system using Cats-Effect
- Implement event sourcing with FS2 streams
- Add configuration with PureConfig
- Comprehensive testing with ScalaTest + ScalaCheck

### **Project 2: RESTful API with Database**
- HTTP endpoints with Http4s
- Database operations with Doobie
- JSON serialization with Circe
- Error handling with Either/ZIO

### **Project 3: Streaming Data Pipeline**
- Real-time data processing with FS2
- Connect to external APIs for data
- Implement retry logic with Cats-Retry
- Monitoring and logging throughout

### **Project 4: Property-Based Testing Suite**
- Comprehensive test suites using ScalaCheck
- Generate edge cases automatically
- Mock external dependencies with ScalaMock
- CI/CD integration with test coverage reporting

---

## 🎯 Impact on Professional Development

### **Before Libraries**
```scala
// Manual, error-prone implementations
def combineLists[A](list1: List[A], list2: List[A]): List[A] =
  list1 ++ list2  // Simple, but what about error handling?

case class Config(port: Int)  // No validation or loading
def loadConfig(): Config = ???  // Manual parsing required

def processStream(data: List[String]): List[String] =
  data.map(_.toUpperCase)  // What about errors or async processing?
```

### **After Libraries**
```scala
import cats.implicits._  // Rich combinators
import pureconfig._      // Type-safe config
import fs2._             // Professional streaming

// Type-safe, composable, error-handling aware
def combineLists[A: Semigroup](list1: List[A], list2: List[A]): List[A] =
  list1.combine(list2)  // Uses semigroup law, error-aware

case class Config(port: Int, host: String)
val config = ConfigSource.default.loadOrThrow[Config]  // Type-safe loading

def processStream[F[_]: Concurrent](data: Stream[F, String]): Stream[F, String] =
  data.mapAsync(4)(s => IO(s.toUpperCase).delayBy(100.millis))
    .handleErrorWith(logErrorAndSkip)  // Robust error handling
```

### **Professional Code Quality Improvement**
- **Type Safety**: Compile-time guarantees
- **Error Handling**: Comprehensive and composable
- **Async Programming**: Proper concurrency management
- **Testing**: Property-based and behavior-driven
- **Composition**: Algebraic data types and type classes
- **Performance**: Streaming and optimized operations

---

*"Great libraries don't just solve problems—they enable new ways of thinking and building applications that are more robust, maintainable, and scalable."*

**Master these libraries, and you'll be able to build enterprise-grade Scala applications with confidence and elegance.**

[← Back to Main Curriculum](../README.md)
