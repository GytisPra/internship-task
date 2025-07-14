import upickle.default.read
import os.write as osWrite
import os.Path
import ujson.{Arr, Value, Obj}

import com.internshiptask.Utils.{GeoUtils, ResultUtils}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Extensions.ArgsExtensions.{getOutputPathOrExit, getInputPathOrExit}

@main
def main(args: String*): Unit =
  if !os.exists(os.pwd / "output") then os.makeDir(os.pwd / "output")

  val regionsPath   = args.getInputPathOrExit(prefix = "regions=")
  val locationsPath = args.getInputPathOrExit(prefix = "locations=")
  val outputPath    = args.getOutputPathOrExit(prefix = "output=")

  val regions   = read[Either[String, List[Region]]](os.read(regionsPath)) match {
    case Left(error)    =>
      println(s"Error: $error")
      sys.exit(1)
    case Right(regions) => regions
  }
  val locations = read[Either[String, List[Location]]](os.read(locationsPath)) match {
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

  ResultUtils.writeResults(outputPath, results)
