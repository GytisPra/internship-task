package com.internshiptask.Models

import upickle.default.{reader, Reader}
import ujson.{Value, Arr, Obj}

case class Point(x: Double, y: Double)

object Point {
  import upickle.default.{Reader, macroR}

  implicit val pointReader: Reader[Point] =
    reader[(Double, Double)].map[Point]((x, y) => new Point(x, y))
}
