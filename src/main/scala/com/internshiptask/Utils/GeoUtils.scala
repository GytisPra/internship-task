package com.internshiptask.Utils

import com.internshiptask.Models.{Location, Polygon, Precision}
import com.internshiptask.Models.Coordinate
import com.internshiptask.Models.Point

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

    if edges.exists((p1, p2) => isPointOnEdge(locY, locX, p1, p2))
    then true
    else
      edges
        .map((p1, p2) =>
          // (locY < p1.y) != (locY < p2.y) Checks if the given location is not above or belove an edge
          // p1.x + ((locY - p1.y) / (p2.y - p1.y)) * (p2.x - p1.x) calculates the intersection point,
          // then we check if the location is to the left of the intersection
          if (locY < p1.y) != (locY < p2.y) && locX < p1.x + ((locY - p1.y) / (p2.y - p1.y)) * (p2.x - p1.x)
          then 1
          else 0
        )
        .sum % 2 == 1

  def isPointOnEdge(locY: Coordinate, locX: Coordinate, p1: Point, p2: Point)(using precision: Precision): Boolean =
    val minX = if p1.x > p2.x then p2.x else p1.x
    val maxX = if p1.x > p2.x then p1.x else p2.x
    val minY = if p1.y > p2.y then p2.y else p1.y
    val maxY = if p1.y > p2.y then p1.y else p2.y

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
}
