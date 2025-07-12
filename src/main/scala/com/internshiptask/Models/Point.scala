package com.internshiptask.Models

import upickle.default.{reader, Reader}
import ujson.{Value, Arr, Obj}

case class Point private(val x: Double, val y: Double)

object Point {
  import upickle.default.{Reader, macroR}

  implicit val pointReader: Reader[Either[String, Point]] =
    reader[(Double, Double)].map[Either[String, Point]]((long, lat) => 
      Point(long, lat)
    )

  def apply(long: Double, lat: Double): Either[String, Point] =
    if long > 180 || long < -180 then Left(s"provided longitude ($long) cannot be more than 180 or less than -180 degrees")
    else if lat > 90 || lat < -90 then Left(s"provided latitude ($lat) cannot be more than 90 or less than -90 degrees")
    else Right(new Point(long, lat))

  def unsafeApply(long: Double, lat: Double): Point =
    new Point(long, lat)
}
