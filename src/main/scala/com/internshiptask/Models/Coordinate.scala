package com.internshiptask.Models

case class Precision(val p: Double)

case class Coordinate(val coord: Double):
  def +(other: Coordinate): Coordinate = Coordinate(coord + other.coord)
  def -(other: Coordinate): Coordinate = Coordinate(coord - other.coord)
  def *(other: Coordinate): Coordinate = Coordinate(coord * other.coord)
  def /(other: Coordinate): Coordinate = Coordinate(coord / other.coord)
  
  def >(other: Coordinate)(using precision: Precision): Boolean =
    (coord - other.coord) > precision.p

  def <(other: Coordinate)(using precision: Precision): Boolean =
    (other.coord - coord) > precision.p

  def >=(other: Coordinate)(using precision: Precision): Boolean =
    (this > other) || (this ~= other)

  def <=(other: Coordinate)(using precision: Precision): Boolean =
    (this < other) || (this ~= other)

  def ~=(other: Coordinate)(using precision: Precision): Boolean =
    (other.coord - coord).abs <= precision.p
