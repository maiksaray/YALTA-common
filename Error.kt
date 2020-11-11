package common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class Error(open val message: String)

@Serializable
data class InvalidCredentials(override val message: String) : Error(message)

@Serializable
data class Unauthorized(override val message: String) : Error(message)

fun encode(err: Error) = Json.encodeToString(err)
