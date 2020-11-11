package common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class Error(open val message: String)

@Serializable
data class InvalidCredentials(val icMessage: String) : Error(icMessage)

@Serializable
data class Unauthorized(val uMessage: String) : Error(uMessage)

fun encode(err: Error) = Json.encodeToString(err)
