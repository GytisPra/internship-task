package Models

import upickle.default.Reader

case class Location(val name: String, val coordinates: (Double, Double)) derives Reader
