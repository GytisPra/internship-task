import upickle.default.read
import os.write as osWrite
import os.Path
import ujson.{Arr, Value, Obj}
import java.nio.file.NotDirectoryException

import com.internshiptask.Utils.{GeoUtils, DirName, ResultUtils}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Extensions.ArgsExtensions.getPath
import scala.util.Try
import scala.util.Failure
import scala.util.Success

@main
def main(args: String*): Unit =
  val inputDir  = DirName("input") match 
    case Left(exception) => throw exception
    case Right(dirName) => dirName
  val outputDir = DirName("output") match
    case Left(exception) => throw exception
    case Right(dirName) => dirName

  val regionsPath   = args.getPath(prefix = "regions=", dir = inputDir)
  val locationsPath = args.getPath(prefix = "locations=", dir = inputDir)
  val outputPath    = args.getPath(prefix = "output=", dir = outputDir)

  val regions: List[Region]     = read[List[Region]](os.read(regionsPath))
  val locations: List[Location] = read[List[Location]](os.read(locationsPath))

  val unformattedResults = for
    region   <- regions
    location <- locations
    if GeoUtils.locationInPolygons(location, region.polygons)
  yield (region.name, location.name)

  val results = ResultUtils.formatResults(regions, unformattedResults)

  ResultUtils.writeResults(outputPath, results)
