// JSON serialization and deserialization codecs
// Demonstrates Circe usage with functional programming

package restapiserver

import io.circe._
import io.circe.generic.semiauto._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Custom datetime codec
private val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

implicit val zonedDateTimeCodec: Codec[ZonedDateTime] = Codec.from(
  Decoder.decodeString.emap { str =>
    try Right(ZonedDateTime.parse(str, dateTimeFormatter))
    catch {
      case _: Exception => Left(s"Invalid datetime format: $str. Expected ISO format like: 2023-12-10T10:15:30Z")
    }
  },
  Encoder.encodeString.contramap[ZonedDateTime](dt => dt.format(dateTimeFormatter))
)

// Request encoders/decoders
implicit val createUserRequestCodec: Codec[CreateUserRequest] = deriveCodec[CreateUserRequest]

implicit val createTaskRequestCodec: Codec[CreateTaskRequest] = deriveCodec[CreateTaskRequest]

implicit val updateTaskRequestCodec: Codec[UpdateTaskRequest] = deriveCodec[UpdateTaskRequest]

// Response encoders/decoders
implicit val userResponseCodec: Codec[UserResponse] = deriveCodec[UserResponse]

implicit val taskResponseCodec: Codec[TaskResponse] = deriveCodec[TaskResponse]

// Error and success responses
implicit val errorResponseCodec: Codec[ErrorResponse] = deriveCodec[ErrorResponse]

implicit val successResponseCodec: Codec[SuccessResponse] = deriveCodec[SuccessResponse]

// Pagination
implicit val paginationInfoCodec: Codec[PaginationInfo] = deriveCodec[PaginationInfo]

implicit def paginatedResponseCodec[T: Codec]: Codec[PaginatedResponse[T]] = deriveCodec[PaginatedResponse[T]]

// Custom encoders for specific use cases
object CustomCodecs {
  // Encoder for validation errors
  implicit val validationErrorsEncoder: Encoder[List[String]] = Encoder.encodeList[String]

  // Decoder for path parameters (UUID)
  def uuidDecoder(failureMessage: String): Decoder[java.util.UUID] =
    Decoder.decodeString.emap { str =>
      try Right(java.util.UUID.fromString(str))
      catch {
        case _: Exception => Left(failureMessage)
      }
    }

  // Simple decoders for query parameters
  val intQueryParamDecoder: Decoder[Int] = Decoder.decodeString.emap { str =>
    try Right(str.toInt)
    catch {
      case _: NumberFormatException => Left(s"Invalid integer: $str")
    }
  }

  val stringQueryParamDecoder: Decoder[String] = Decoder.decodeString
}
