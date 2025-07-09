import upickle.default.{write, read}
import os.write as osWrite
import ujson.{Arr, Value, Obj}
import java.io.FileNotFoundException

import Models.*
import Utils.GeoUtils

@main
def main(regionsPath: String, locationsPath: String, outputPath: String): Unit =
  // I am guessing this is bad because the user doesn't know in what order they should input stuff
  // so maybe replacing this with args array and then from there finding the paths would be better?
  val locInputPath =
    os.pwd / "input" / locationsPath.stripPrefix("locations=")
  val regInputPath =
    os.pwd / "input" / regionsPath.stripPrefix("regions=")
  var resPath      =
    os.pwd / "output" / outputPath.stripPrefix("output=")

  if !os.exists(locInputPath) then
    throw FileNotFoundException(s"$locInputPath does not exist")

  if !os.exists(regInputPath) then
    throw FileNotFoundException(s"$regInputPath does not exist")

  val regions: List[Region]     = read[List[Region]](os.read(regInputPath))
  val locations: List[Location] = read[List[Location]](os.read(locInputPath))

  val unformattedResults = for
    region   <- regions
    location <- locations
    if GeoUtils.locationInPolygons(location, region.polygons)
  yield (region.name, location.name)

  val results = formatResults(regions, unformattedResults)

  osWrite(resPath, write[List[Result]](results))
