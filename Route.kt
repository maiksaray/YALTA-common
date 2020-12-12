package common

import java.sql.Timestamp
import java.util.ArrayList

data class Point(val id: Long?, val lat: Double, val lon: Double, val name: String)

data class RoutePoint(val id: Long?, val point: Point, val visited: Boolean)

data class Route(val id: Long?,
                 val driverId: Long?,
                 val routeDate: Timestamp,
                 val points: ArrayList<RoutePoint>,
                 val finished: Boolean)
