import upickle.default.read
import com.typesafe.scalalogging.Logger
import scala.util.{Try, Failure, Success}
import java.io.File

import com.internshiptask.Utils.{GeoUtils, ResultUtils, CliParser}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Config.ScoptConfig

@main
def main(args: String*): Unit =
  given logger: Logger = Logger("InternshipTaskLogger")

  CliParser.parse(args) match
    case Left(error)   => logger.error(error)
    case Right(config) =>
      runApp(config) match
        case Left(error) => logger.error(error)
        case Right(_)    => logger.info(s"Output saved to ${config.outputFile.getAbsolutePath}")

def runApp(config: ScoptConfig): Either[String, Unit] =
  val locationsFile = config.locationsFile
  val regionsFile   = config.regionsFile
  val outputFile    = config.outputFile

  for
    regions   <- readRegionsJson(regionsFile)
    locations <- readLocationsJson(locationsFile)
  yield
    val unformattedResults = for
      region   <- regions
      location <- locations
      if GeoUtils.locationInPolygons(location, region.polygons)
    yield (region.name, location.name)

    val results = Result.formatResults(regions, unformattedResults)

    ResultUtils.writeResults(outputFile, results)

def readLocationsJson(file: File): Either[String, List[Location]] =
  Try(read[Either[String, List[Location]]](file)) match
    case Failure(e)       => Left(s"Error occured while parsing ${file.getName}: ${e.getMessage}")
    case Success(results) => results

def readRegionsJson(file: File): Either[String, List[Region]] =
  Try(read[Either[String, List[Region]]](file)) match
    case Failure(e)       => Left(s"Error occured while parsing ${file.getName}: ${e.getMessage}")
    case Success(results) => results
