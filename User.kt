package common

sealed class Role
object Driver : Role()
object Admin : Role()

data class User(val id: Long?, val name: String, val password: String, val role: Role?)