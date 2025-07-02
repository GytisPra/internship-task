//> using toolkit default

import upickle.default.*

case class Location(name: String, coordinates: List[Float]) derives ReadWriter

case class Region(name: String, coordinates: List[List[List[Float]]]) derives ReadWriter

@main
def main(): Unit = 
    val locationsInputPath = os.pwd / "input/locations.json"
    val regionsInputPath = os.pwd / "input/regions.json"

    println(s"Attempting to read locations from directory ${locationsInputPath}")
    println(s"Attempting to read regions from directory ${regionsInputPath}")

    val locations: List[Location] = read[List[Location]](os.read(locationsInputPath))
    val regions: List[Region] = read[List[Region]](os.read(regionsInputPath))

    regions.map(r => println(r))

    