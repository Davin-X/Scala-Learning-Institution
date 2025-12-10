// HTTP Routes using HTTP4s
// Demonstrates functional web endpoints with type-safe routing

package restapiserver

import org.http4s._
import org.http4s.dsl.Http4sDsl
import cats.effect.Sync
import com.typesafe.scalalogging.LazyLogging
import cats.syntax.all._
import java.util.UUID
import scala.util.control.NonFatal

class UserRoutes[F[_]: Sync](
  userService: UserService[F],
  corsConfig: CorsConfig
) extends Http4sDsl[F] with LazyLogging with JsonCodecs[F] {

  private def withErrorHandling(routes: HttpRoutes[F]): HttpRoutes[F] = HttpRoutes.of {
    case request =>
      logger.debug(s"Incoming ${request.method} request to: ${request.uri.path}")
      routes.run(request).handleErrorWith {
        case NonFatal(error) =>
          logger.error("Unhandled error in user routes", error)
          InternalServerError(ErrorResponse("Internal server error", error.getMessage))
      }
  }

  val routes: HttpRoutes[F] = withErrorHandling(HttpRoutes.of[F] {

    // Health check
    case GET -> Root / "health" =>
      logger.debug("Health check request")
      Ok(SuccessResponse(message = "API is healthy"))

    // GET /api/v1/users - List users with pagination
    case GET -> Root / "api" / "v1" / "users" :? OptionalLimitQueryParamMatcher(limit) +& OptionalOffsetQueryParamMatcher(offset) =>
      val limitVal = limit.getOrElse(20)
      val offsetVal = offset.getOrElse(0)

      for {
        users <- userService.getAllUsers(limitVal, offsetVal)
        response = users.map(new UserResponse(_))
        paginated = PaginatedResponse(
          response,
          PaginationInfo(
            page = offsetVal / limitVal + 1,
            limit = limitVal,
            total = response.length, // Note: In real impl, get total count separately
            totalPages = (response.length + limitVal - 1) / limitVal
          )
        )
        result <- Ok(paginated)
      } yield result

    // POST /api/v1/users - Create user
    case req @ POST -> Root / "api" / "v1" / "users" =>
      for {
        request <- req.as[CreateUserRequest]
        result <- userService.createUser(request)
        response <- result match {
          case Right(user) => Created(new UserResponse(user))
          case Left(errors) => BadRequest(ErrorResponse("Validation failed", errors.mkString(", ")))
        }
      } yield response

    // GET /api/v1/users/:id - Get user by ID
    case GET -> Root / "api" / "v1" / "users" / UUIDVar(id) =>
      for {
        userOpt <- userService.getUserById(UserId(id))
        response <- userOpt match {
          case Some(user) => Ok(new UserResponse(user))
          case None => NotFound(ErrorResponse("User not found", s"No user with id $id"))
        }
      } yield response

    // PUT /api/v1/users/:id/status - Update user status
    case req @ PUT -> Root / "api" / "v1" / "users" / UUIDVar(id) / "status" =>
      for {
        statusStr <- req.as[Map[String, String]].map(_("status"))
        status <- TaskStatus.fromString(statusStr) match { // Note: reusing TaskStatus enum for user status
          case Some(s) => UserStatus.fromString(s.toString).getOrElse(UserStatus.Active).pure[F]
          case None => UserStatus.Active.pure[F] // Default fallback
        }
        result <- userService.updateUser(UserId(id), status)
        response <- result match {
          case Right(user) => Ok(new UserResponse(user))
          case Left(error) => NotFound(ErrorResponse("Update failed", error))
        }
      } yield response

    // DELETE /api/v1/users/:id - Delete user
    case DELETE -> Root / "api" / "v1" / "users" / UUIDVar(id) =>
      for {
        result <- userService.deleteUser(UserId(id))
        response <- result match {
          case Right(_) => NoContent()
          case Left(error) => NotFound(ErrorResponse("Delete failed", error))
        }
      } yield response
  })

  // Query parameter matchers
  object OptionalLimitQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OptionalOffsetQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("offset")

  // Path parameter matcher for UUID
  object UUIDVar {
    def unapply(str: String): Option[UUID] = {
      try Some(UUID.fromString(str))
      catch {
        case _: IllegalArgumentException => None
      }
    }
  }
}

class TaskRoutes[F[_]: Sync](
  taskService: TaskService[F],
  userService: UserService[F]
) extends Http4sDsl[F] with LazyLogging with JsonCodecs[F] {

  private def withErrorHandling(routes: HttpRoutes[F]): HttpRoutes[F] = HttpRoutes.of {
    case request =>
      logger.debug(s"Incoming ${request.method} request to: ${request.uri.path}")
      routes.run(request).handleErrorWith {
        case ValidationErrors(errors) =>
          logger.warn(s"Validation failed: ${errors.mkString(", ")}")
          BadRequest(ErrorResponse("Validation failed", errors.mkString(", ")))
        case NotFoundError(message) =>
          logger.info(s"Resource not found: $message")
          NotFound(ErrorResponse("Not found", message))
        case NonFatal(error) =>
          logger.error("Unhandled error in task routes", error)
          InternalServerError(ErrorResponse("Internal server error", error.getMessage))
      }
  }

  val routes: HttpRoutes[F] = withErrorHandling(HttpRoutes.of[F] {

    // GET /api/v1/tasks - List tasks with pagination
    case GET -> Root / "api" / "v1" / "tasks" :? OptionalLimitQueryParamMatcher(limit) +& OptionalOffsetQueryParamMatcher(offset) +& OptionalAssigneeQueryParamMatcher(assigneeEmail) =>
      val limitVal = limit.getOrElse(20)
      val offsetVal = offset.getOrElse(0)

      for {
        tasks <- assigneeEmail match {
          case Some(email) =>
            // Find user by email and get their tasks
            userService.getUserByEmail(Email(email)).flatMap {
              case Some(user) => taskService.getTasksByAssignee(user.id, limitVal, offsetVal)
              case None => List.empty[Task].pure[F]
            }
          case None => taskService.getAllTasks(limitVal, offsetVal)
        }

        // Get assignee emails for responses
        taskResponses <- tasks.traverse { task =>
          task.assigneeId match {
            case Some(userId) =>
              userService.getUserById(userId).map { userOpt =>
                val email = userOpt.map(_.email.value)
                new TaskResponse(task, email)
              }
            case None => new TaskResponse(task, None).pure[F]
          }
        }

        paginated = PaginatedResponse(
          taskResponses,
          PaginationInfo(
            page = offsetVal / limitVal + 1,
            limit = limitVal,
            total = taskResponses.length,
            totalPages = (taskResponses.length + limitVal - 1) / limitVal
          )
        )
        result <- Ok(paginated)
      } yield result

    // POST /api/v1/tasks - Create task
    case req @ POST -> Root / "api" / "v1" / "tasks" =>
      for {
        request <- req.as[CreateTaskRequest]
        result <- taskService.createTask(request)
        response <- result match {
          case Right(task) =>
            // Get assignee email for response
            task.assigneeId match {
              case Some(userId) =>
                userService.getUserById(userId).flatMap { userOpt =>
                  val email = userOpt.map(_.email.value)
                  Created(new TaskResponse(task, email))
                }
              case None => Created(new TaskResponse(task, None))
            }
          case Left(errors) => BadRequest(ErrorResponse("Validation failed", errors.mkString(", ")))
        }
      } yield response

    // GET /api/v1/tasks/:id - Get task by ID
    case GET -> Root / "api" / "v1" / "tasks" / UUIDVar(id) =>
      for {
        taskOpt <- taskService.getTaskById(TaskId(id))
        response <- taskOpt match {
          case Some(task) =>
            // Get assignee email
            task.assigneeId match {
              case Some(userId) =>
                userService.getUserById(userId).flatMap { userOpt =>
                  val email = userOpt.map(_.email.value)
                  Ok(new TaskResponse(task, email))
                }
              case None => Ok(new TaskResponse(task, None))
            }
          case None => NotFound(ErrorResponse("Task not found", s"No task with id $id"))
        }
      } yield response

    // PUT /api/v1/tasks/:id - Update task
    case req @ PUT -> Root / "api" / "v1" / "tasks" / UUIDVar(id) =>
      for {
        updateRequest <- req.as[UpdateTaskRequest]
        result <- taskService.updateTask(TaskId(id), updateRequest)
        response <- result match {
          case Right(task) =>
            // Get assignee email
            task.assigneeId match {
              case Some(userId) =>
                userService.getUserById(userId).flatMap { userOpt =>
                  val email = userOpt.map(_.email.value)
                  Ok(new TaskResponse(task, email))
                }
              case None => Ok(new TaskResponse(task, None))
            }
          case Left(errors) => BadRequest(ErrorResponse("Validation failed", errors.mkString(", ")))
        }
      } yield response

    // DELETE /api/v1/tasks/:id - Delete task
    case DELETE -> Root / "api" / "v1" / "tasks" / UUIDVar(id) =>
      for {
        result <- taskService.deleteTask(TaskId(id))
        response <- result match {
          case Right(_) => NoContent()
          case Left(error) => NotFound(ErrorResponse("Delete failed", error))
        }
      } yield response

    // PUT /api/v1/tasks/:id/assign - Assign task to user
    case req @ PUT -> Root / "api" / "v1" / "tasks" / UUIDVar(taskId) / "assign" =>
      for {
        assignData <- req.as[Map[String, String]]
        assigneeEmail = assignData.get("assigneeEmail")
        assigneeId <- assigneeEmail match {
          case Some(email) =>
            userService.getUserByEmail(Email(email)).map(_.map(_.id))
          case None => None.pure[F]
        }
        result <- taskService.assignTask(TaskId(taskId), assigneeId)
        response <- result match {
          case Right(task) =>
            // Get assignee email for response
            val email = assigneeId.flatMap(id => Option(email)).orElse(None) // Simplified
            userService.getUserById(task.assigneeId.getOrElse(UserId(UUID.randomUUID()))).flatMap { userOpt =>
              val assigneeEmail = task.assigneeId.flatMap(_ => userOpt.map(_.email.value))
              Ok(new TaskResponse(task, assigneeEmail))
            }
          case Left(error) => BadRequest(ErrorResponse("Assignment failed", error))
        }
      } yield response

  })

  // Query parameter matchers
  object OptionalLimitQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OptionalOffsetQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object OptionalAssigneeQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("assigneeEmail")

  // Path parameter matcher for UUID
  object UUIDVar {
    def unapply(str: String): Option[UUID] = {
      try Some(UUID.fromString(str))
      catch {
        case _: IllegalArgumentException => None
      }
    }
  }
}

// Custom error types for better error handling
case class ValidationErrors(errors: List[String]) extends RuntimeException(s"Validation errors: ${errors.mkString(", ")}")
case class NotFoundError(message: String) extends RuntimeException(message)
