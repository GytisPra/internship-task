package com.internshiptask.Models

case class Polygon(val points: List[Point]):
  def getEdges(): List[(Point, Point)] =
    points zip (points.tail :+ points.head) map { case ((p1), (p2)) => (p1, p2) }

object Polygon {
  import upickle.default.{reader, read, Reader}
  import ujson.{Arr}

  implicit val polygonReader: Reader[Either[String, Polygon]] =
    reader[Arr].map[Either[String, Polygon]](coordinates =>
      val result           = read[List[Either[String, Point]]](coordinates)
      val (errors, points) = result.partitionMap(identity)

      if errors.nonEmpty then Left(s"error occured while parsing polygon: ${errors.mkString(", ")}")
      else Right(Polygon(points))
    )
}
