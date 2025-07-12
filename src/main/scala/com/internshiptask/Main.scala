import upickle.default.read
import os.write as osWrite
import os.Path
import ujson.{Arr, Value, Obj}
import java.nio.file.NotDirectoryException

import com.internshiptask.Utils.{GeoUtils, ResultUtils}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Extensions.ArgsExtensions.getPath
import scala.util.Try
import scala.util.Failure
import scala.util.Success

@main
def main(args: String*): Unit =
  val inputDir  = "input"
  val outputDir = "output"

  val regionsPath = args.getPath(prefix = "regions=", dir = inputDir) match {
    case Left(exception) => throw exception
    case Right(path)     => path
  }

  val locationsPath = args.getPath(prefix = "locations=", dir = inputDir) match {
    case Left(exception) => throw exception
    case Right(path)     => path
  }

  val outputPath = args.getPath(prefix = "output=", dir = outputDir) match {
    case Left(exception) => throw exception
    case Right(path)     => path
  }

  val regions   = read[Either[String, List[Region]]](os.read(regionsPath)) match {
    case Left(error)    => throw new Exception(error)
    case Right(regions) => regions
  }
  val locations = read[Either[String, List[Location]]](os.read(locationsPath)) match {
    case Left(error)      => throw new Exception(error)
    case Right(locations) => locations
  }

  val unformattedResults = for
    region   <- regions
    location <- locations
    if GeoUtils.locationInPolygons(location, region.polygons)
  yield (region.name, location.name)

  val results = ResultUtils.formatResults(regions, unformattedResults)

  ResultUtils.writeResults(outputPath, results)
