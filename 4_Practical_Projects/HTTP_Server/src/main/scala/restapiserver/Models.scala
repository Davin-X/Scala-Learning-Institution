// Domain models and data transfer objects
// Demonstrates Scala case classes, validation, and serialization

package restapiserver

import java.time.ZonedDateTime
import java.util.UUID

// Domain Entities
case class User(
  id: UserId,
  email: Email,
  name: String,
  createdAt: ZonedDateTime = ZonedDateTime.now(),
  status: UserStatus = UserStatus.Active
) {
  def isActive: Boolean = status == UserStatus.Active
}

case class Task(
  id: TaskId,
  title: String,
  description: Option[String],
  status: TaskStatus = TaskStatus.Pending,
  priority: Priority = Priority.Medium,
  assigneeId: Option[UserId] = None,
  createdAt: ZonedDateTime = ZonedDateTime.now(),
  dueDate: Option[ZonedDateTime] = None
) {
  def isOverdue: Boolean = dueDate.exists(_.isBefore(ZonedDateTime.now()))
  def belongsTo(user: User): Boolean = assigneeId.contains(user.id)
}

// Value Objects
case class UserId(value: UUID) extends AnyVal {
  def toString: String = value.toString
}

case class TaskId(value: UUID) extends AnyVal {
  def toString: String = value.toString
}

case class Email(value: String) extends AnyVal {
  def isValid: Boolean = value.contains("@") && value.length <= 254
  def domain: String = value.split("@").tail.headOption.getOrElse("")
}

// Enumerations with sealed traits
sealed trait UserStatus
object UserStatus {
  case object Active extends UserStatus
  case object Inactive extends UserStatus
  case object Suspended extends UserStatus

  def fromString(str: String): Option[UserStatus] = str.toLowerCase match {
    case "active" => Some(Active)
    case "inactive" => Some(Inactive)
    case "suspended" => Some(Suspended)
    case _ => None
  }

  def toString(status: UserStatus): String = status match {
    case Active => "active"
    case Inactive => "inactive"
    case Suspended => "suspended"
  }
}

sealed trait TaskStatus
object TaskStatus {
  case object Pending extends TaskStatus
  case object InProgress extends TaskStatus
  case object Completed extends TaskStatus
  case object Cancelled extends TaskStatus

  def fromString(str: String): Option[TaskStatus] = str.toLowerCase.replace("_", "").replace(" ", "") match {
    case "pending" => Some(Pending)
    case "inprogress" | "inprogress" => Some(InProgress)
    case "completed" => Some(Completed)
    case "cancelled" | "canceled" => Some(Cancelled)
    case _ => None
  }

  def toString(status: TaskStatus): String = status match {
    case Pending => "pending"
    case InProgress => "in_progress"
    case Completed => "completed"
    case Cancelled => "cancelled"
  }
}

sealed trait Priority
object Priority {
  case object Low extends Priority
  case object Medium extends Priority
  case object High extends Priority
  case object Urgent extends Priority

  def fromString(str: String): Option[Priority] = str.toLowerCase match {
    case "low" => Some(Low)
    case "medium" => Some(Medium)
    case "high" => Some(High)
    case "urgent" => Some(Urgent)
    case _ => None
  }

  def toString(priority: Priority): String = priority match {
    case Low => "low"
    case Medium => "medium"
    case High => "high"
    case Urgent => "urgent"
  }
}

// API Request/Response DTOs
case class CreateUserRequest(
  email: String,
  name: String
) {
  def toValidated: Either[List[String], CreateUserRequest] = {
    val errors = List.newBuilder[String]

    if (!email.contains("@")) {
      errors += "Email must be valid"
    }

    if (name.trim.isEmpty) {
      errors += "Name cannot be empty"
    }

    if (name.trim.length < 2) {
      errors += "Name must be at least 2 characters"
    }

    val errorList = errors.result()
    if (errorList.isEmpty) Right(this) else Left(errorList)
  }
}

case class CreateTaskRequest(
  title: String,
  description: Option[String],
  priority: Option[String],
  assigneeEmail: Option[String]
) {
  def toValidated: Either[List[String], CreateTaskRequest] = {
    val errors = List.newBuilder[String]

    if (title.trim.isEmpty) {
      errors += "Title cannot be empty"
    }

    if (title.trim.length > 200) {
      errors += "Title must be less than 200 characters"
    }

    description.foreach { desc =>
      if (desc.length > 2000) {
        errors += "Description must be less than 2000 characters"
      }
    }

    priority.foreach { p =>
      if (Priority.fromString(p).isEmpty) {
        errors += "Priority must be one of: low, medium, high, urgent"
      }
    }

    assigneeEmail.foreach { email =>
      if (!email.contains("@")) {
        errors += "Assignee email must be valid"
      }
    }

    val errorList = errors.result()
    if (errorList.isEmpty) Right(this) else Left(errorList)
  }
}

case class UpdateTaskRequest(
  title: Option[String],
  description: Option[String],
  status: Option[String],
  priority: Option[String],
  assigneeEmail: Option[String]
) {
  def toValidated: Either[List[String], UpdateTaskRequest] = {
    val errors = List.newBuilder[String]

    title.foreach { t =>
      if (t.trim.isEmpty) {
        errors += "Title cannot be empty"
      }
      if (t.trim.length > 200) {
        errors += "Title must be less than 200 characters"
      }
    }

    description.foreach { desc =>
      if (desc.length > 2000) {
        errors += "Description must be less than 2000 characters"
      }
    }

    status.foreach { s =>
      if (TaskStatus.fromString(s).isEmpty) {
        errors += "Status must be one of: pending, in_progress, completed, cancelled"
      }
    }

    priority.foreach { p =>
      if (Priority.fromString(p).isEmpty) {
        errors += "Priority must be one of: low, medium, high, urgent"
      }
    }

    assigneeEmail.foreach { email =>
      if (!email.contains("@")) {
        errors += "Assignee email must be valid"
      }
    }

    val errorList = errors.result()
    if (errorList.isEmpty) Right(this) else Left(errorList)
  }
}

// Response DTOs
case class UserResponse(
  id: String,
  email: String,
  name: String,
  status: String,
  createdAt: String
) {
  def this(user: User) = this(
    id = user.id.toString,
    email = user.email.value,
    name = user.name,
    status = UserStatus.toString(user.status),
    createdAt = user.createdAt.toString
  )
}

case class TaskResponse(
  id: String,
  title: String,
  description: Option[String],
  status: String,
  priority: String,
  assigneeEmail: Option[String],
  createdAt: String,
  dueDate: Option[String],
  isOverdue: Boolean
) {
  def this(task: Task, assigneeEmail: Option[String] = None) = this(
    id = task.id.toString,
    title = task.title,
    description = task.description,
    status = TaskStatus.toString(task.status),
    priority = Priority.toString(task.priority),
    assigneeEmail = assigneeEmail,
    createdAt = task.createdAt.toString,
    dueDate = task.dueDate.map(_.toString),
    isOverdue = task.isOverdue
  )
}

// Error handling
case class ErrorResponse(
  error: String,
  message: String,
  timestamp: String = ZonedDateTime.now().toString
)

// Success responses
case class SuccessResponse(
  success: Boolean = true,
  message: String,
  data: Option[String] = None,
  timestamp: String = ZonedDateTime.now().toString
)

// Pagination
case class PaginationInfo(
  page: Int,
  limit: Int,
  total: Long,
  totalPages: Int
)

case class PaginatedResponse[T](
  data: List[T],
  pagination: PaginationInfo
)
