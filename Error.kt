package common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class Error{abstract val message: String}

@Serializable
data class InvalidCredentials(override val message:String) : Error()

@Serializable
data class Unauthorized(override val message: String) : Error()

fun encode(err: Error) = Json.encodeToString(err)
