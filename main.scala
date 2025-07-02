//> using toolkit 0.7.0

import upickle.default.*
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import os.write.append

case class Location(name: String, coordinates: (Double, Double)) derives Reader

case class Region(name: String, @upickle.implicits.key("coordinates") polygons: Vector[Vector[(Double, Double)]]) derives Reader

case class Result(region: String, var matchedLocations: Seq[String]) derives Writer:
    def appendLocation(location: String): Unit =
        this.matchedLocations = this.matchedLocations :+ location

@main
def main(regions: String, locations: String, output: String): Unit = 
    val locationsInputPath = os.pwd / "input" / locations.stripPrefix("locations=")
    val regionsInputPath = os.pwd / "input" / regions.stripPrefix("regions=")
    var resultsPath = os.pwd / "output" / output.stripPrefix("output=")

    if os.exists(resultsPath) then
        os.remove(resultsPath)

    val readLocations: List[Location] = read[List[Location]](os.read(locationsInputPath))
    val readRegions: List[Region] = read[List[Region]](os.read(regionsInputPath))
    var results: List[Result] = List()

    readRegions.map(reg => 
        val result = new Result(reg.name, List())
        readLocations.map(loc => 
            if locationInPolygon(loc, reg) then
                result.appendLocation(loc.name)
                println(s"${reg.name}: ${loc.name}")
        )
        results = results :+ result  
    )

    os.write(resultsPath, write(results))


def locationInPolygon(location: Location, region: Region): Boolean =
    val path: Path2D = new Path2D.Double
    var matchedLocation = ""
   
    region.polygons.map { pol =>
        path.moveTo(pol.head._1, pol.head._2)

        pol.drop(1).map(coords => 
            path.lineTo(coords._1, coords._2)
        )

        path.closePath()

        val testPoint: Point2D = new Point2D.Double(location.coordinates._1, location.coordinates._2)

        if path.contains(testPoint) then
            println(s"${location.name} is inside ${region.name}")
            matchedLocation = location.name
    }

    return matchedLocation != ""