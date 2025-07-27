package com.internshiptask.Utils

import com.internshiptask.Models.{Location, Polygon, Precision, Point}
import com.internshiptask.Models.PointComponent.{min, max}

object GeoUtils {

  /** Checks if a given location is inside any provided polygon
    *
    * This is done using the ray casting algorithm. We take our location and cast a ray to the right
    * side of it to infinty. Now we can imangine what happens when a point is outside of a polygon,
    * the ray will first enter the polygon and then exit so it intersects an even number of times,
    * however if the point is inside the polygon then it will only exit intersecting, an odd number
    * of times.
    *
    * @param location
    *   The location to check
    * @param polygons
    *   A list of polygons
    * @return
    *   True if inside; False otherwise
    */
  def locationInPolygons(
      location: Location,
      polygons: List[Polygon]
  )(using precision: Precision): Boolean =
    polygons
      .map(polygon => locationInPolygon(location, polygon))
      .find(_ == true) != None

  def locationInPolygon(
      location: Location,
      polygon: Polygon
  )(using precision: Precision): Boolean =
    val edges        = polygon.getEdges()
    val (locX, locY) = (location.coordinates.x, location.coordinates.y)

    if !isPointInsideBoundingBox(location.coordinates, polygon.points) then false
    else if edges.exists(isPointOnEdge(location.coordinates, _))
    then true
    else
      edges
        .map((p1, p2) =>
          // (locY < y1) != (locY < y2) Checks if the given location is not above or belove an edge
          // x1 + ((locY - y1) / (y2 - y1)) * (x2 - x1) calculates the intersection point,
          // then we check if the location is to the left of the intersection
          val (x1, y1, x2, y2) = (p1.x, p1.y, p2.x, p2.y)
          if (locY < y1) != (locY < y2) && locX < x1 + ((locY - y1) / (y2 - y1)) * (x2 - x1) then 1
          else 0
        )
        .sum % 2 == 1

  def isPointOnEdge(point: Point, edge: (Point, Point))(using
      precision: Precision
  ): Boolean =
    val (locX, locY) = (point.x, point.y)
    val (p1, p2)     = (edge._1, edge._2)

    val minX = min(p1.x, p2.x)
    val minY = min(p1.y, p2.y)

    val maxX = max(p1.x, p2.x)
    val maxY = max(p1.y, p2.y)

    // this checks if we are inside the bounding box
    if locY >= minY && locY <= maxY && locX >= minX && locX <= maxX then
      if p2.x ~= p1.x then // vertical line
        locX ~= p1.x // no need to check Y because we are inside the bounding box
      else                 // diagonal line
        // f(x) = ax + b
        val a = (p2.y - p1.y) / (p2.x - p1.x)
        val b = p1.y - a * p1.x

        a * locX + b ~= locY
    else false

  def isPointInsideBoundingBox(point: Point, polygonPoints: List[Point])(using
      precision: Precision
  ): Boolean =
    val (locX, locY) = (point.x, point.y)
    val allX         = polygonPoints.map(_.x)
    val allY         = polygonPoints.map(_.y)

    val maxY = allY.max
    val maxX = allX.max
    val minY = allY.min
    val minX = allX.min

    locX >= minX && locX <= maxX && locY >= minY && locY <= maxY
}
