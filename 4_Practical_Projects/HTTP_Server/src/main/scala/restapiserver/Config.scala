// Configuration management using PureConfig
// Demonstrates type-safe configuration with functional programming

package restapiserver

import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.duration.FiniteDuration

case class ApiConfig(
  host: String,
  port: Int,
  shutdownTimeout: FiniteDuration,
  threadPool: ThreadPoolConfig
)

case class ThreadPoolConfig(
  corePoolSize: Int,
  maxPoolSize: Int,
  keepAliveTime: FiniteDuration
)

case class DatabaseConfig(
  driver: String,
  url: String,
  username: String,
  password: String,
  maxConnections: Int,
  connectionTimeout: FiniteDuration
)

case class LoggingConfig(
  level: String,
  http: HttpLoggingConfig
)

case class HttpLoggingConfig(
  enabled: Boolean,
  logHeaders: Boolean,
  logBody: Boolean
)

case class SecurityConfig(
  cors: CorsConfig,
  rateLimit: RateLimitConfig
)

case class CorsConfig(
  allowOrigin: List[String],
  allowMethods: List[String],
  allowHeaders: List[String]
)

case class RateLimitConfig(
  requestsPerMinute: Long,
  burstLimit: Int
)

case class AppConfig(
  name: String,
  version: String,
  security: SecurityConfig
)

case class ServerConfig(
  api: ApiConfig,
  database: DatabaseConfig,
  logging: LoggingConfig,
  app: AppConfig
)

object ServerConfig {
  // Load configuration from file
  def load(): Either[String, ServerConfig] = {
    ConfigSource.default.load[ServerConfig] match {
      case Right(config) => Right(config)
      case Left(errors) =>
        val errorMessages = errors.toList.map(_.description)
        Left(s"Configuration errors: ${errorMessages.mkString("; ")}")
    }
  }

  // Get default configuration for development
  def default(): ServerConfig = ServerConfig(
    api = ApiConfig(
      host = "0.0.0.0",
      port = 8080,
      shutdownTimeout = scala.concurrent.duration.Duration.fromNanos(30000000000L), // 30 seconds
      threadPool = ThreadPoolConfig(4, 16, scala.concurrent.duration.Duration.fromNanos(60000000000L)) // 60 seconds
    ),
    database = DatabaseConfig(
      driver = "org.h2.Driver",
      url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      username = "sa",
      password = "",
      maxConnections = 10,
      connectionTimeout = scala.concurrent.duration.Duration.fromNanos(30000000000L)
    ),
    logging = LoggingConfig(
      level = "INFO",
      http = HttpLoggingConfig(true, false, false)
    ),
    app = AppConfig(
      name = "Scala REST API Server",
      version = "1.0.0",
      security = SecurityConfig(
        cors = CorsConfig(List("*"), List("GET", "POST", "PUT", "DELETE", "OPTIONS"), List("Content-Type", "Authorization")),
        rateLimit = RateLimitConfig(60, 20)
      )
    )
  )
}
