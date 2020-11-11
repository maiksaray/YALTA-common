package common

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
sealed class Role

@Serializable
object Driver : Role()

@Serializable
object Admin : Role()

@Serializable
data class User(val id: Long?, val name: String, @Transient val password: String ="", val role: Role?)

fun encode(user: User): String =
        Json.encodeToString(user)


