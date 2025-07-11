package com.internshiptask.Extensions

import org.scalactic.exceptions.NullArgumentException
import java.io.FileNotFoundException
import os.Path

import com.internshiptask.Utils.DirName

object ArgsExtensions {
  extension (args: Seq[String])
    def getPath(prefix: String, dir: DirName): Path =
      args.find(_.startsWith(prefix)) match {
        case None        => throw NullArgumentException(s"argument with prefix $prefix not found")
        case Some(value) =>
          if value.stripPrefix(prefix).isEmpty then throw IllegalArgumentException("empty path segment not allowed")

          val path = os.pwd / dir.string / value.stripPrefix(prefix)

          if os.exists(path) then path
          else throw new FileNotFoundException(s"file $path does not exist")
      }
}
