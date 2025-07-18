import upickle.default.read

import com.internshiptask.Utils.{GeoUtils, ResultUtils, CliParser}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Config.ScoptConfig

@main
def main(args: String*): Unit =
  val config = CliParser.parse(args)

  val locationsPath = config.locationsFile.toPath
  val regionsPath   = config.regionsFile.toPath
  Option(config.outputFile.getParentFile).foreach(_.mkdirs())

  val regions   = read[Either[String, List[Region]]](regionsPath) match {
    case Left(error)    =>
      println(s"Error: $error")
      sys.exit(1)
    case Right(regions) => regions
  }
  val locations = read[Either[String, List[Location]]](locationsPath) match {
    case Left(error)      =>
      println(s"Error: $error")
      sys.exit(1)
    case Right(locations) => locations
  }

  val unformattedResults = for
    region   <- regions
    location <- locations
    if GeoUtils.locationInPolygons(location, region.polygons)
  yield (region.name, location.name)

  val results = Result.formatResults(regions, unformattedResults)

  ResultUtils.writeResults(config.outputFile, results)
