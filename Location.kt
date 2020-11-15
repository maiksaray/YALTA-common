package common

import java.sql.Timestamp

data class Location(val id: Long?, val lat: Double, val lon: Double, val userId: Long, val timestamp: Timestamp)

data class LocationUpdate(val lat:Double, val lon: Double)
