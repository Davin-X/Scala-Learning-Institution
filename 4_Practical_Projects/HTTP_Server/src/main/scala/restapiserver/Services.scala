// Business logic services
// Demonstrates functional programming with error handling

package restapiserver

import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import java.util.UUID

// Service traits defining contracts
trait UserService[F[_]] {
  def createUser(request: CreateUserRequest): F[Either[List[String], User]]
  def getUserById(id: UserId): F[Option[User]]
  def getUserByEmail(email: Email): F[Option[User]]
  def getAllUsers(limit: Int, offset: Int): F[List[User]]
  def updateUser(id: UserId, status: UserStatus): F[Either[String, User]]
  def deleteUser(id: UserId): F[Either[String, Unit]]
}

trait TaskService[F[_]] {
  def createTask(request: CreateTaskRequest): F[Either[List[String], Task]]
  def getTaskById(id: TaskId): F[Option[Task]]
  def getAllTasks(limit: Int, offset: Int): F[List[Task]]
  def getTasksByAssignee(assigneeId: UserId, limit: Int, offset: Int): F[List[Task]]
  def updateTask(id: TaskId, request: UpdateTaskRequest): F[Either[List[String], Task]]
  def deleteTask(id: TaskId): F[Either[String, Unit]]
  def assignTask(taskId: TaskId, assigneeId: Option[UserId]): F[Either[String, Task]]
}

// Repository traits for data access
trait UserRepository[F[_]] {
  def save(user: User): F[User]
  def findById(id: UserId): F[Option[User]]
  def findByEmail(email: Email): F[Option[User]]
  def findAll(limit: Int, offset: Int): F[List[User]]
  def updateStatus(id: UserId, status: UserStatus): F[Option[User]]
  def delete(id: UserId): F[Boolean]
}

trait TaskRepository[F[_]] {
  def save(task: Task): F[Task]
  def findById(id: TaskId): F[Option[Task]]
  def findAll(limit: Int, offset: Int): F[List[Task]]
  def findByAssignee(assigneeId: UserId, limit: Int, offset: Int): F[List[Task]]
  def update(task: Task): F[Option[Task]]
  def delete(id: TaskId): F[Boolean]
}

// In-memory implementations for demo
class InMemoryUserRepository[F[_]: Sync] extends UserRepository[F] with LazyLogging {
  private val users = Ref.of[F, Map[UserId, User]](Map.empty).unsafeRunSync()

  override def save(user: User): F[User] = {
    logger.debug(s"Saving user: ${user.email.value}")
    for {
      _ <- users.update(_ + (user.id -> user))
    } yield user
  }

  override def findById(id: UserId): F[Option[User]] = {
    logger.debug(s"Finding user by ID: $id")
    users.get.map(_.get(id))
  }

  override def findByEmail(email: Email): F[Option[User]] = {
    logger.debug(s"Finding user by email: ${email.value}")
    users.get.map(_.values.find(_.email == email))
  }

  override def findAll(limit: Int, offset: Int): F[List[User]] = {
    logger.debug(s"Getting all users with limit: $limit, offset: $offset")
    users.get.map(_.values.toList.drop(offset).take(limit))
  }

  override def updateStatus(id: UserId, status: UserStatus): F[Option[User]] = {
    logger.debug(s"Updating user $id status to $status")
    for {
      updatedOpt <- users.modify { usersMap =>
        usersMap.get(id) match {
          case Some(user) =>
            val updated = user.copy(status = status)
            (usersMap + (id -> updated), Some(updated))
          case None =>
            (usersMap, None)
        }
      }
      _ = updatedOpt.foreach(user => logger.info(s"User ${user.email.value} status updated to $status"))
    } yield updatedOpt
  }

  override def delete(id: UserId): F[Boolean] = {
    logger.debug(s"Deleting user: $id")
    users.modify { usersMap =>
      val exists = usersMap.contains(id)
      val updated = usersMap - id
      (updated, exists)
    }
  }
}

class InMemoryTaskRepository[F[_]: Sync] extends TaskRepository[F] with LazyLogging {
  private val tasks = Ref.of[F, Map[TaskId, Task]](Map.empty).unsafeRunSync()

  override def save(task: Task): F[Task] = {
    logger.debug(s"Saving task: ${task.title}")
    for {
      _ <- tasks.update(_ + (task.id -> task))
    } yield task
  }

  override def findById(id: TaskId): F[Option[Task]] = {
    logger.debug(s"Finding task by ID: $id")
    tasks.get.map(_.get(id))
  }

  override def findAll(limit: Int, offset: Int): F[List[Task]] = {
    logger.debug(s"Getting all tasks with limit: $limit, offset: $offset")
    tasks.get.map(_.values.toList.drop(offset).take(limit))
  }

  override def findByAssignee(assigneeId: UserId, limit: Int, offset: Int): F[List[Task]] = {
    logger.debug(s"Finding tasks for assignee: $assigneeId")
    tasks.get.map(_.values.toList.filter(_.assigneeId.contains(assigneeId)).drop(offset).take(limit))
  }

  override def update(task: Task): F[Option[Task]] = {
    logger.debug(s"Updating task: ${task.id}")
    for {
      updatedOpt <- tasks.modify { tasksMap =>
        val updated = tasksMap.get(task.id).map(_ => task)
        val newMap = updated.map(t => tasksMap + (t.id -> t)).getOrElse(tasksMap)
        (newMap, updated)
      }
    } yield updatedOpt
  }

  override def delete(id: TaskId): F[Boolean] = {
    logger.debug(s"Deleting task: $id")
    tasks.modify { tasksMap =>
      val exists = tasksMap.contains(id)
      val updated = tasksMap - id
      (updated, exists)
    }
  }
}

// Service implementations
class UserServiceImpl[F[_]: Sync](
  userRepo: UserRepository[F]
) extends UserService[F] with LazyLogging {

  override def createUser(request: CreateUserRequest): F[Either[List[String], User]] = {
    request.toValidated match {
      case Right(validRequest) =>
        for {
          existingUser <- userRepo.findByEmail(Email(validRequest.email))
          result <- existingUser match {
            case Some(_) =>
              logger.warn(s"User with email ${validRequest.email} already exists")
              Left(List("User with this email already exists")).pure[F]
            case None =>
              val newUser = User(
                id = UserId(UUID.randomUUID()),
                email = Email(validRequest.email),
                name = validRequest.name.trim
              )
              userRepo.save(newUser).map(Right(_))
          }
        } yield result
      case Left(errors) => Left(errors).pure[F]
    }
  }

  override def getUserById(id: UserId): F[Option[User]] = {
    userRepo.findById(id)
  }

  override def getUserByEmail(email: Email): F[Option[User]] = {
    userRepo.findByEmail(email)
  }

  override def getAllUsers(limit: Int, offset: Int): F[List[User]] = {
    userRepo.findAll(limit = math.min(limit, 100), offset) // Max 100 users per page
  }

  override def updateUser(id: UserId, status: UserStatus): F[Either[String, User]] = {
    userRepo.updateStatus(id, status).map {
      case Some(user) => Right(user)
      case None => Left(s"User with id $id not found")
    }
  }

  override def deleteUser(id: UserId): F[Either[String, Unit]] = {
    userRepo.delete(id).map {
      case true => Right(())
      case false => Left(s"User with id $id not found")
    }
  }
}

class TaskServiceImpl[F[_]: Sync](
  taskRepo: TaskRepository[F],
  userRepo: UserRepository[F]
) extends TaskService[F] with LazyLogging {

  override def createTask(request: CreateTaskRequest): F[Either[List[String], Task]] = {
    request.toValidated match {
      case Right(validRequest) =>
        for {
          assigneeOpt <- validRequest.assigneeEmail match {
            case Some(email) => userRepo.findByEmail(Email(email))
            case None => Option.empty[User].pure[F]
          }
          task = Task(
            id = TaskId(UUID.randomUUID()),
            title = validRequest.title.trim,
            description = validRequest.description.map(_.trim),
            priority = validRequest.priority.flatMap(Priority.fromString).getOrElse(Priority.Medium),
            assigneeId = assigneeOpt.map(_.id)
          )
          saved <- taskRepo.save(task)
        } yield Right(saved)
      case Left(errors) => Left(errors).pure[F]
    }
  }

  override def getTaskById(id: TaskId): F[Option[Task]] = {
    taskRepo.findById(id)
  }

  override def getAllTasks(limit: Int, offset: Int): F[List[Task]] = {
    taskRepo.findAll(limit = math.min(limit, 100), offset)
  }

  override def getTasksByAssignee(assigneeId: UserId, limit: Int, offset: Int): F[List[Task]] = {
    taskRepo.findByAssignee(assigneeId, math.min(limit, 100), offset)
  }

  override def updateTask(id: TaskId, request: UpdateTaskRequest): F[Either[List[String], Task]] = {
    request.toValidated match {
      case Right(validRequest) =>
        for {
          existingTaskOpt <- taskRepo.findById(id)
          result <- existingTaskOpt match {
            case Some(existingTask) =>
              val updatedTask = existingTask.copy(
                title = validRequest.title.getOrElse(existingTask.title),
                description = validRequest.description.orElse(existingTask.description),
                status = validRequest.status.flatMap(TaskStatus.fromString).getOrElse(existingTask.status),
                priority = validRequest.priority.flatMap(Priority.fromString).getOrElse(existingTask.priority)
              )
              taskRepo.update(updatedTask).map(taskOpt =>
                taskOpt.toRight(List("Failed to update task"))
              )
            case None => Left(List("Task not found")).pure[F]
          }
        } yield result
      case Left(errors) => Left(errors).pure[F]
    }
  }

  override def deleteTask(id: TaskId): F[Either[String, Unit]] = {
    taskRepo.delete(id).map {
      case true => Right(())
      case false => Left(s"Task with id $id not found")
    }
  }

  override def assignTask(taskId: TaskId, assigneeId: Option[UserId]): F[Either[String, Task]] = {
    for {
      taskOpt <- taskRepo.findById(taskId)
      result <- taskOpt match {
        case Some(task) =>
          // Validate assignee exists if one is specified
          assigneeId match {
            case Some(assignee) =>
              userRepo.findById(assignee).flatMap {
                case Some(_) =>
                  val updatedTask = task.copy(assigneeId = assigneeId)
                  taskRepo.update(updatedTask).map {
                    case Some(updated) => Right(updated)
                    case None => Left("Failed to assign task")
                  }
                case None => Left("Assignee user not found").pure[F]
              }
            case None =>
              val updatedTask = task.copy(assigneeId = None)
              taskRepo.update(updatedTask).map {
                case Some(updated) => Right(updated)
                case None => Left("Failed to unassign task")
              }
          }
        case None => Left("Task not found").pure[F]
      }
    } yield result
  }
}
