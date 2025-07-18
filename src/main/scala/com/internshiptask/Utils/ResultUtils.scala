package com.internshiptask.Utils

import com.internshiptask.Models.{Region, Result}
import upickle.default.write
import java.io.File
import java.io.PrintWriter
import os.Path

object ResultUtils {
  def writeResults(outputFile: File, results: List[Result]): Unit =
    os.write.over(Path(outputFile.getAbsolutePath), write[List[Result]](results))
}
