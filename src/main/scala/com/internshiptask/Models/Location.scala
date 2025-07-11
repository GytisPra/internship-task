package com.internshiptask.Models

import upickle.default.{Reader, reader, read}
import ujson.{Value, Obj}

case class Location(val name: String, val coordinates: Point)

object Location {
  implicit val locationsReader: Reader[Location] =
    reader[Value].map[Location](json =>
      val name  = json("name").str
      val point = read[Point](json("coordinates"))
      new Location(name, point)
    )
}
