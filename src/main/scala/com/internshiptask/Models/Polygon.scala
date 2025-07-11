package com.internshiptask.Models

case class Polygon(val points: List[Point]):
  def getEdges(): List[(Point, Point)] =
    points zip (points.tail :+ points.head) map { case ((p1), (p2)) => (p1, p2) }

object Polygon {
  import upickle.default.{reader, read, Reader}
  import ujson.{Arr}

  implicit val polygonReader: Reader[Polygon] = reader[Arr].map[Polygon](coordinates =>
    val points = read[List[Point]](coordinates)
    Polygon(points)
  )
}
