import upickle.default.{write, read}
import os.write as osWrite
import os.Path
import ujson.{Arr, Value, Obj}
import java.io.FileNotFoundException

import Models.*
import Utils.GeoUtils

// TODO: Read the articles attached in the email and see if I can make any improvements

@main
def main(args: String*): Unit =
  var paths: Map[String, Path] = Map()

  args.map(arg =>
    arg match
      case value if value.contains("regions=")   =>
        val path = os.pwd / "input" / arg.stripPrefix("regions=")
        paths += ("regions" -> path)
      case value if value.contains("locations=") =>
        val path = os.pwd / "input" / arg.stripPrefix("locations=")
        paths += ("locations" -> path)
      case value if value.contains("output=")    =>
        val path = os.pwd / "output" / arg.stripPrefix("output=")
        paths += ("output" -> path)
      case other                                 =>
        println(s"Unknown argument provided $other")
        System.exit(1)
  )

  if !os.exists(paths("locations")) then
    throw FileNotFoundException(s"${paths("locations")} does not exist")

  if !os.exists(paths("regions")) then
    throw FileNotFoundException(s"${paths("regions")} does not exist")

  val regions: List[Region]     = read[List[Region]](os.read(paths("regions")))
  val locations: List[Location] = read[List[Location]](os.read(paths("locations")))

  val unformattedResults = for
    region   <- regions
    location <- locations
    if GeoUtils.locationInPolygons(location, region.polygons)
  yield (region.name, location.name)

  val results = formatResults(regions, unformattedResults)

  if os.exists(paths("output")) then os.remove(paths("output"))

  osWrite(paths("output"), write[List[Result]](results))
