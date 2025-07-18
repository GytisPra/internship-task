package com.internshiptask.Utils

import upickle.default.write
import java.io.File
import os.Path

import com.internshiptask.Models.{Region, Result}

object ResultUtils {
  def writeResults(outputFile: File, results: List[Result]): Unit =
    os.write.over(Path(outputFile.getAbsolutePath), write[List[Result]](results))
}
