package com.internshiptask.Utils

import com.internshiptask.Models.{Region, Result}
import os.Path
import upickle.default.write

object ResultUtils {
  def formatResults(
      regions: List[Region],
      unformattedResults: List[(String, String)]
  ): List[Result] =
    val groupedAndTransformedResults = unformattedResults
      .groupBy(r => r._1) // Group by the region name
      .transform((key, value) => // for the value we only need the location
        value.map(valueTuple => valueTuple._2)
      )
      .map(resultTuple => Result(resultTuple._1, resultTuple._2))

    regions.map(region =>
      groupedAndTransformedResults.find(_.region == region.name) match
        case None        => Result(region.name, List())
        case Some(value) => value
    )

  def writeResults(outputPath: Path, results: List[Result]): Unit =
    if os.exists(outputPath) then os.remove(outputPath)
    os.write(outputPath, write[List[Result]](results))
}
