package com.internshiptask.Models

case class Precision(val p: Double)

case class PointComponent(val value: Double)(using precision: Precision = Precision(1e-6))
    extends Comparable[PointComponent]:
  def +(other: PointComponent): PointComponent = PointComponent(value + other.value)
  def -(other: PointComponent): PointComponent = PointComponent(value - other.value)
  def *(other: PointComponent): PointComponent = PointComponent(value * other.value)
  def /(other: PointComponent): PointComponent = PointComponent(value / other.value)

  def >(other: PointComponent)(using precision: Precision): Boolean =
    (value - other.value) > precision.p

  def <(other: PointComponent)(using precision: Precision): Boolean =
    (other.value - value) > precision.p

  def >=(other: PointComponent)(using precision: Precision): Boolean =
    (this > other) || (this ~= other)

  def <=(other: PointComponent)(using precision: Precision): Boolean =
    (this < other) || (this ~= other)

  def ~=(other: PointComponent)(using precision: Precision): Boolean =
    (other.value - value).abs <= precision.p

  override def compareTo(o: PointComponent): Int =
    if this > o then 1
    else if this ~= o then 0
    else -1

object PointComponent {
  def max(value1: PointComponent, value2: PointComponent)(using
      precision: Precision
  ): PointComponent =
    if value1 ~= value2 then value1
    else if value1 > value2 then value1
    else value2

  def min(value1: PointComponent, value2: PointComponent)(using
      precision: Precision
  ): PointComponent =
    if value1 ~= value2 then value1
    else if value1 > value2 then value2
    else value1
}
