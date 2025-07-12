package com.internshiptask.Models

import upickle.default.Writer

case class Result(val region: String, val matchedLocations: List[String]) derives Writer
