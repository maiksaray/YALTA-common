package common

import org.joda.time.DateTime

data class Point(val id: Long?, val lat: Double, val lon: Double, val name: String)

data class RoutePoint(val id: Long?, val point: Point, val visited: Boolean, val index:Long)

data class Route(val id: Long?,
                 val driverId: Long?,
                 val routeDate: DateTime,
                 val points: List<RoutePoint>,
                 val finished: Boolean)

data class CreateRoute(val routeDate: DateTime, val driverId: Long?, val points: List<Point>)

data class AssignDriver(val driverId: Long)

data class UpdatePoint(val newState: Boolean)