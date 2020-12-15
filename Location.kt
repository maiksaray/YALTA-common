package common

import org.joda.time.DateTime

data class Location(val id: Long?, val lat: Double, val lon: Double, val userId: Long, val timestamp: DateTime)

data class OffsetedLocationUpdate(val lat: Double, val lon: Double, val secondsOffset: Double)

data class LocationUpdate(val lat: Double, val lon: Double)
