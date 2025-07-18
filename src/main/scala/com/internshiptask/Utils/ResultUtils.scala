package com.internshiptask.Utils

import com.internshiptask.Models.{Region, Result}
import upickle.default.write
import java.io.File
import os.Path

object ResultUtils {
  def writeResults(outputFile: File, results: List[Result]): Unit =
    if outputFile.exists then outputFile.delete

    os.write(Path(outputFile.getAbsolutePath), write[List[Result]](results))
}
