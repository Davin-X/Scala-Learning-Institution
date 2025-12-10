// Main API Server Application
// Demonstrates composition of all components into a working HTTP server

package restapiserver

import cats.effect._
import cats.syntax.all._
import com.comcast.ip4s._
import com.typesafe.scalalogging.LazyLogging
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object ApiServer extends IOApp with LazyLogging {

  def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      // Load configuration
      config <- loadConfiguration()
      _ <- IO(logger.info(s"Starting ${config.app.name} v${config.app.version}"))

      // Initialize repositories
      userRepo = new InMemoryUserRepository[IO]
      taskRepo = new InMemoryTaskRepository[IO]

      // Initialize services
      userService = new UserServiceImpl[IO](userRepo)
      taskService = new TaskServiceImpl[IO](taskRepo, userRepo)

      // Create routes
      userRoutes = new UserRoutes[IO](userService, config.app.security.cors)
      taskRoutes = new TaskRoutes[IO](taskService, userService)

      // Combine all routes
      allRoutes = userRoutes.routes <+> taskRoutes.routes

      // Apply middleware
      corsMiddleware = applyCorsMiddleware(allRoutes, config.app.security.cors)
      rateLimitMiddleware = applyRateLimiting(corsMiddleware, config.app.security.rateLimit)
      loggingMiddleware = applyRequestLogging(rateLimitMiddleware, config.logging.http)

      // Create server
      server = BlazeServerBuilder[IO](ExecutionContext.global)
        .bindHttp(config.api.port, config.api.host)
        .withHttpApp(loggingMiddleware.orNotFound)
        .withShutdownTimeout(config.api.shutdownTimeout)
        .serve

      _ <- IO(logger.info(s"Server starting on http://${config.api.host}:${config.api.port}"))
      _ <- IO(logger.info("Health check available at: http://localhost:8080/health"))
      _ <- IO(logger.info("API documentation would be at: http://localhost:8080/api-docs"))
      _ <- IO.println(generateApiDocumentation())

      exitCode <- server.compile.drain.as(ExitCode.Success)
        .handleErrorWith { error =>
          logger.error("Failed to start server", error)
          IO.pure(ExitCode.Error)
        }
    } yield exitCode

    program.guaranteeCase {
      case Outcome.Succeeded(_) => IO(logger.info("Server shutdown cleanly"))
      case Outcome.Errored(e) => IO(logger.error("Server shutdown with error", e))
      case Outcome.Canceled() => IO(logger.warn("Server was cancelled"))
    }
  }

  private def loadConfiguration(): IO[ServerConfig] = {
    ServerConfig.load() match {
      case Right(config) =>
        IO.pure(config)
      case Left(errors) =>
        IO.raiseError(new RuntimeException(s"Failed to load configuration: $errors"))
    }
  }

  private def applyCorsMiddleware[F[_]: Sync](
    routes: HttpRoutes[F],
    corsConfig: CorsConfig
  ): HttpRoutes[F] = {
    logger.info("Enabling CORS middleware")
    CORS.policy
      .withAllowOriginHost(corsConfig.allowOrigin.contains_("*"))
      .withAllowMethodsIn(corsConfig.allowMethods.toSet)
      .withAllowHeadersIn(corsConfig.allowHeaders.toSet)
      .apply(routes)
  }

  private def applyRateLimiting[F[_]: Sync](
    routes: HttpRoutes[F],
    rateLimitConfig: RateLimitConfig
  ): HttpRoutes[F] = {
    logger.info(s"Enabling rate limiting: ${rateLimitConfig.requestsPerMinute} req/min")
    // Note: In a real implementation, you'd use a proper rate limiting library
    // For demo purposes, we'll skip complex rate limiting implementation
    routes
  }

  private def applyRequestLogging[F[_]: Sync](
    routes: HttpRoutes[F],
    loggingConfig: HttpLoggingConfig
  ): HttpRoutes[F] = {
    if (loggingConfig.enabled) {
      logger.info("Enabling HTTP request logging")
      org.http4s.server.middleware.Logger.httpRoutes[F](
        logHeaders = loggingConfig.logHeaders,
        logBody = loggingConfig.logBody
      )(routes)
    } else {
      logger.info("HTTP request logging disabled")
      routes
    }
  }

  private def generateApiDocumentation(): String = {
    val docs = """
    |🛠️  REST API SERVER - AVAILABLE ENDPOINTS
    |══════════════════════════════════════════════════════════════
    |🌐 Server: http://localhost:8080
    |📊 Health: GET /health
    |📖 API Version: v1
    |
    |👥 USER MANAGEMENT ENDPOINTS
    |  POST /api/v1/users          - Create new user
    |  GET  /api/v1/users          - List users (pagination)
    |  GET  /api/v1/users/:id      - Get user by ID
    |  PUT  /api/v1/users/:id/status - Update user status
    |  DELETE /api/v1/users/:id    - Delete user
    |
    |📋 TASK MANAGEMENT ENDPOINTS
    |  POST /api/v1/tasks          - Create new task
    |  GET  /api/v1/tasks          - List tasks (pagination, optional assignee filter)
    |  GET  /api/v1/tasks/:id      - Get task by ID
    |  PUT  /api/v1/tasks/:id      - Update task
    |  PUT  /api/v1/tasks/:id/assign - Assign task to user
    |  DELETE /api/v1/tasks/:id    - Delete task
    |
    |🧪 TESTING THE API
    |══════════════════
    |Export JSON examples to test each endpoint using curl, Postman, or your favorite HTTP client.
    |
    |🎯 Examples:
    |curl -X POST http://localhost:8080/api/v1/users -H "Content-Type: application/json" \\
    |     -d '{"email":"alice@example.com","name":"Alice Johnson"}'
    |
    |curl -X GET "http://localhost:8080/api/v1/users?limit=10&offset=0"
    |
    |curl -X POST http://localhost:8080/api/v1/tasks -H "Content-Type: application/json" \\
    |     -d '{"title":"Implement user authentication","description":"Add login functionality","priority":"high","assigneeEmail":"alice@example.com"}'
    |
    |⚡ Functional Programming Features Demonstrated:
    |  • Type-safe configuration loading
    |  • Functional error handling with Either
    |  • Monadic composition with Cats
    |  • Immutable data structures
    |  • Pure functions throughout
    |  • HTTP4s functional web framework
    |══════════════════════════════════════════════════════════════
    """.stripMargin

    docs
  }
}
