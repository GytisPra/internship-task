package com.internshiptask.Utils

import com.internshiptask.Models.{Region, Result}
import os.Path
import upickle.default.write

object ResultUtils {
  def writeResults(outputPath: Path, results: List[Result]): Unit =
    if os.exists(outputPath) then os.remove(outputPath)
    os.write(outputPath, write[List[Result]](results))
}
