package common

import org.joda.time.DateTime

data class Point(val id: Long?, val lat: Double, val lon: Double, val name: String)

data class RoutePoint(val id: Long?, val point: Point, val visited: Boolean)

data class Route(val id: Long?,
                 val driverId: Long?,
                 val routeDate: DateTime,
                 val points: java.util.List<RoutePoint>,
                 val finished: Boolean)
