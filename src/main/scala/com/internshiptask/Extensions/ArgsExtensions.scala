package com.internshiptask.Extensions

import org.scalactic.exceptions.NullArgumentException
import java.io.FileNotFoundException
import os.Path

object ArgsExtensions {
  extension (args: Seq[String])
    def getInputPathOrExit(prefix: String): Path =
      args.find(_.startsWith(prefix)) match {
        case None        =>
          println(s"Error: argument with prefix '$prefix' not found")
          sys.exit(1)
        case Some(value) =>
          val path = os.pwd / "input" / value.stripPrefix(prefix)

          if os.exists(path) then path
          else
            println(s"Error: file $path does not exist")
            sys.exit(1)
      }

    def getOutputPathOrExit(prefix: String): Path =
      args.find(_.startsWith(prefix)) match {
        case None        =>
          println(s"Error: argument with prefix '$prefix' not found")
          sys.exit(1)
        case Some(value) =>
          os.pwd / "output" / value.stripPrefix(prefix)
      }
}
