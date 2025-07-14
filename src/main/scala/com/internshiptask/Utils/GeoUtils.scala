package com.internshiptask.Utils

import com.internshiptask.Models.{Location, Polygon}

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
  ): Boolean =
    polygons
      .map(polygon => locationInPolygon(location, polygon))
      .find(_ == true) != None

  def locationInPolygon(
      location: Location,
      polygon: Polygon
  ): Boolean =
    val edges        = polygon.getEdges()
    val (locX, locY) = (location.coordinates.x, location.coordinates.y)

    // check if the location is on an edge
    if edges.exists((p1, p2) => locY >= p1.y && locY <= p2.y && locX >= p1.x && locX <= p2.x)
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
        .foldLeft(0)(_ + _) % 2 == 1
}
