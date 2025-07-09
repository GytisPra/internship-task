package Models

import upickle.default.Writer

case class Result(val region: String, val matchedLocations: List[String])
    derives Writer

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
    .toList

  regions.map(region =>
    groupedAndTransformedResults.find(_.region == region.name) match
      case None        => Result(region.name, List())
      case Some(value) => value
  )