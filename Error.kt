package common


sealed class Error {
    abstract val message: String
}

data class InvalidCredentials(override val message: String) : Error()

data class Unauthorized(override val message: String) : Error()

data class BadRequest(override val message: String) : Error()

object ErrorAdaper {
    val factory = HierarchyTypeAdapterFactory.of(Error::class.java)
            .registerSubtype(InvalidCredentials::class.java)
            .registerSubtype(Unauthorized::class.java)
            .registerSubtype(BadRequest::class.java)
}