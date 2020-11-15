package common

data class VehicleClass(val id: Long?, val name: String)

data class VehicleModel(val id: Long?, val name: String, val vehicleClass: VehicleClass)

data class Vehicle(val id: Long?, val name: String, val licensePLate: String, val model: VehicleModel)
