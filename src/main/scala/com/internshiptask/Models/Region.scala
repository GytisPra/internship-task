package com.internshiptask.Models

import com.internshiptask.Models.Polygon

case class Region(val name: String, val polygons: List[Polygon])

object Region {
  import upickle.default.{reader, read, Reader}
  import ujson.{Value, Arr}

  implicit val regionReader: Reader[Either[String, Region]] =
    reader[Value].map[Either[String, Region]](json =>
      val name        = json("name").str
      val coordinates = json("coordinates").arr
      if name.isBlank then Left("name of a region is blank")
      else if coordinates.isEmpty then Left(s"$name has no polygons")
      else
        val results = read[List[Either[String, Polygon]]](coordinates)

        val (errors, polygons) = results.partitionMap(identity)

        if errors.nonEmpty then
          Left(s"error occured while parsing polygons: ${errors.mkString(", ")}")
        else Right(Region(name, polygons))
    )

  implicit val regionsReader: Reader[Either[String, List[Region]]] =
    reader[Arr].map[Either[String, List[Region]]](arr =>
      val results = arr.value.map(json => read[Either[String, Region]](json))

      val (errors, regions) = results.partitionMap(identity)

      if errors.nonEmpty then
        Left(s"errors occured while parsing regions: ${errors.mkString(", ")}")
      else Right(regions.toList)
    )
}
