package com.internshiptask.Models

import com.internshiptask.Models.Polygon

case class Region(val name: String, val polygons: List[Polygon])

object Region {
  import upickle.default.{reader, read, Reader}
  import ujson.{Value, Arr}

  implicit val regionReader: Reader[Region] =
    reader[Value].map[Region](json =>
      val name     = json("name").str
      val polygons = read[List[Polygon]](json("coordinates"))
      new Region(name, polygons)
    )
}
