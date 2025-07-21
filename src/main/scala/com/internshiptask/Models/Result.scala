package com.internshiptask.Models

import upickle.default.Writer

case class Result(val region: String, val matchedLocations: List[String]) derives Writer

object Result {
  def formatResults(
      regions: List[Region],
      unformattedResults: List[(String, String)]
  ): List[Result] =
    val groupedResults = unformattedResults.groupMap(_._1)(_._2)
    
    regions.map(region => Result(region.name, groupedResults.getOrElse(region.name, List())))
}
