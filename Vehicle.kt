package common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class VehicleClass(val id: Long?, val name: String)

@Serializable
data class VehicleModel(val id: Long?, val name: String, val vehicleClass: VehicleClass)

@Serializable
data class Vehicle(val id: Long?, val name: String, val licensePLate: String, val model: VehicleModel)

fun encode(user: Vehicle): String =
        Json.encodeToString(user)