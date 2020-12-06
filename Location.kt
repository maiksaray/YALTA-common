package common

import java.sql.Timestamp

//TODO: maybe use something newer than java.sql.Timestamp here since we are detached from DB here...
data class Location(val id: Long?, val lat: Double, val lon: Double, val userId: Long, val timestamp: Timestamp)

data class OffsetedLocationUpdate(val lat: Double, val lon: Double, val secondsOffset: Double)

data class LocationUpdate(val lat: Double, val lon: Double)
