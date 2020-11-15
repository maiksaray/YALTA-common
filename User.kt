package common

sealed class Role

object Driver : Role() {
    override fun toString() = "driver"
}

object Admin : Role() {
    override fun toString() = "admin"
}

object RoleAdapter {
    val factory = HierarchyTypeAdapterFactory.of(Role::class.java)
            .registerSubtype(Driver::class.java)
            .registerSubtype(Admin::class.java)
}

data class User(val id: Long?, val name: String, val password: String = "", val role: Role?)


