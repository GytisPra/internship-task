package Utils

import Models.{Location, Polygon}

object GeoUtils {

  /** Checks if a given location is inside any provided polygon
    *
    * This is implement using the ray casting algorithm. We take our location
    * and cast a ray to the right side of it to infinty. Now we can imangine
    * what happens when a point is outside of a polygon, the ray will first
    * enter the polygon and then exit so it intersects an even number of times,
    * however if the point is inside the polygon then it will only exit
    * intersecting, an odd number of times.
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
    val edges    = polygon.getEdges()
    val (xl, yl) = location.coordinates

    // check if the location is on an edge
    if edges.exists((x1, y1, x2, y2) =>
        xl >= x1 && xl <= x2 && yl >= y1 && yl <= y2
      )
    then true
    else
      edges
        .map((x1, y1, x2, y2) =>
          // (yl < y1) != (yl < y2) Checks if the given location is not above or belove an edge
          // x1 + ((yl - y1) / (y2 - y1)) * (x2 - x1) calculates the intersection point, then we check if the point is to the left of the intersection
          if (yl < y1) != (yl < y2) && xl < x1 + ((yl - y1) / (y2 - y1)) * (x2 - x1)
          then 1
          else 0
        )
        .foldLeft(0)(_ + _) % 2 == 1
}
