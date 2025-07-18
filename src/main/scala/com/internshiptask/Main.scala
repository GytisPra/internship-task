import upickle.default.read

import com.internshiptask.Utils.{GeoUtils, ResultUtils, CliParser}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Config.ScoptConfig

@main
def main(args: String*): Unit =
  CliParser.parse(args) match
    case None         => sys.exit(1)
    case Some(config) => runApp(config)

def runApp(config: ScoptConfig): Unit = 
  // safe to call 'get' because the argument is required
  val locationsFile = config.locationsFile.get
  val regionsFile   = config.regionsFile.get
  val outputFile    = config.outputFile.get

  // Make sure the output dir exists
  // Using Option here because the parentFile can be null
  Option(outputFile.getParentFile).foreach(_.mkdirs())

  val regions   = read[Either[String, List[Region]]](regionsFile) match {
    case Left(error)    =>
      println(s"Error: $error")
      sys.exit(1)
    case Right(regions) => regions
  }
  val locations = read[Either[String, List[Location]]](locationsFile) match {
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

  ResultUtils.writeResults(outputFile, results)