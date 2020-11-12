package common

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
sealed class Role

@Serializable
object Driver : Role() {
    override fun toString() = "driver"
}

@Serializable
object Admin : Role() {
    override fun toString() = "admin"
}

@Serializable
data class User(val id: Long?, val name: String, @Transient val password: String = "", val role: Role?)

fun encode(user: User): String =
    Json.encodeToString(user)


