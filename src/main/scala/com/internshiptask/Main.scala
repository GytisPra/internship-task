import upickle.default.read
import com.typesafe.scalalogging.Logger
import scala.util.{Failure, Success}

import com.internshiptask.Utils.{GeoUtils, ResultUtils, CliParser}
import com.internshiptask.Models.{Location, Region, Result}
import com.internshiptask.Config.ScoptConfig

@main
def main(args: String*): Unit =
  val logger: Logger = Logger("InternshipTaskLogger")

  val results = CliParser.parse(args) match
    case None         => sys.exit(1)
    case Some(config) => runApp(config)

  results match
    case Left(error) => logger.error(error)
    case Right(_)    => sys.exit(0)

def runApp(config: ScoptConfig): Either[String, Unit] =
  // safe to call 'get' because the argument is required
  val locationsFile = config.locationsFile.get
  val regionsFile   = config.regionsFile.get
  val outputFile    = config.outputFile.get

  // Make sure the output dir exists
  // Using Option here because the parentFile can be null
  Option(outputFile.getParentFile).foreach(_.mkdirs())

  for 
    regions <- read[Either[String, List[Region]]](regionsFile)
    locations <- read[Either[String, List[Location]]](locationsFile)
  yield
    val unformattedResults = for
      region   <- regions
      location <- locations
      if GeoUtils.locationInPolygons(location, region.polygons)
    yield (region.name, location.name)

    val results = Result.formatResults(regions, unformattedResults)

    ResultUtils.writeResults(outputFile, results)
