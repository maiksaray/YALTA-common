package common

data class VehicleClassO(val id: Long, val name: String)

data class VehicleModelO(val id: Long, val name: String, val vehicleClass: VehicleClassO)

data class VehicleO(val id: Long, val name: String, val licensePLate: String, val model: VehicleModelO)