package com.internshiptask.Extensions

import org.scalactic.exceptions.NullArgumentException
import java.io.FileNotFoundException
import os.Path


object ArgsExtensions {
  extension (args: Seq[String])
    def getPath(prefix: String, dir: String): Path =
      args.find(_.startsWith(prefix)) match {
        case None        => throw NullArgumentException(s"argument with prefix $prefix not found")
        case Some(value) =>
          val path = os.pwd / dir / value.stripPrefix(prefix)

          if os.exists(path) then path
          else throw new FileNotFoundException(s"file $path does not exist")
      }
}
