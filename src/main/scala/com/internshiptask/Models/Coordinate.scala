package com.internshiptask.Models

case class Precision(val p: Double)

case class Coordinate(val coord: Double)(using precision: Precision = Precision(1e-6))
    extends Comparable[Coordinate]:
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

  override def compareTo(o: Coordinate): Int =
    if this > o then 1
    else if this ~= o then 0
    else -1

object Coordinate {
  def max(coord1: Coordinate, coord2: Coordinate)(using precision: Precision): Coordinate =
    if coord1 ~= coord2 then coord1
    else if coord1 > coord2 then coord1
    else coord2

  def min(coord1: Coordinate, coord2: Coordinate)(using precision: Precision): Coordinate =
    if coord1 ~= coord2 then coord1
    else if coord1 > coord2 then coord2
    else coord1
}
