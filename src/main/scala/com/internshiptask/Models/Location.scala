package com.internshiptask.Models

case class Location(val name: String, val coordinates: Point)

object Location {
  import upickle.default.{Reader, reader, read}
  import ujson.{Value, Obj, Arr}

  implicit val locationReader: Reader[Either[String, Location]] =
    reader[Value].map[Either[String, Location]](json =>
      val name = json("name").str
      read[Either[String, Point]](json("coordinates")) match
        case Left(error)  => Left(s"error while parsing point: $error")
        case Right(point) => Right(new Location(name, point))
    )

  implicit val locationsReader: Reader[Either[String, List[Location]]] =
    reader[Arr].map[Either[String, List[Location]]](value =>
      val results = value match {
        case Arr(value) => value.map(json => read[Either[String, Location]](json))
      }

      val (errors, locations) = results.partitionMap(identity)

      if errors.nonEmpty then
        Left(s"errors occured while parsing locations: ${errors.mkString(", ")}")
      else Right(locations.toList)
    )
}
