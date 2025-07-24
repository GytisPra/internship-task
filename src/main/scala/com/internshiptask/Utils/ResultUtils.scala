package com.internshiptask.Utils

import java.io.{PrintWriter, File}

import com.internshiptask.Models.{Region, Result}

object ResultUtils {
  def writeResults(outputFile: File, results: List[Result]): Unit =
    new PrintWriter(outputFile) {
      write(upickle.default.write[List[Result]](results))
      close
    }
}
