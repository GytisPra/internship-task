package Models

import Models.Polygon

case class Region(
    val name: String,
    val polygons: List[Polygon]
)

object Region {
  import upickle.default.{reader, read, Reader}
  import ujson.{Value, Obj, Arr}

  implicit val regionReader: Reader[Region] =
    reader[Value].map[Region] {
      case Obj(json) =>
        val name     = json("name").str
        val polygons = read[List[Polygon]](json("coordinates"))
        new Region(name, polygons)
      case other     =>
        throw new Error(s"exptected ujson.Obj(json) got: $other")
    }
}
