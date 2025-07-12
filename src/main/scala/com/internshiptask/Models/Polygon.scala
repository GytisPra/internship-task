package com.internshiptask.Models

case class Polygon(val points: List[Point]):
  def getEdges(): List[(Point, Point)] =
    points zip (points.tail :+ points.head) map { case ((p1), (p2)) => (p1, p2) }

object Polygon {
  import upickle.default.{reader, read, Reader}
  import ujson.{Arr}

  implicit val polygonReader: Reader[Either[String, Polygon]] =
    reader[Arr].map[Either[String, Polygon]](coordinates =>
      val points = read[List[Either[String, Point]]](coordinates)

      val errors = points.map {
        case Left(error) => Some(error)
        case Right(point) => None
      }.flatten

      val validPoints = points.map {
        case Left(error) => None
        case Right(value) => Some(value)
      }.flatten

      if errors.length > 0 then
        Left(s"error occured while parsing points: $errors")
      else
        Right(Polygon(validPoints))
    )
}
