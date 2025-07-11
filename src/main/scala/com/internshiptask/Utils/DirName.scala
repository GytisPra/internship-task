package com.internshiptask.Utils

import java.nio.file.NotDirectoryException
import os.Path

class DirName private (val string: String)
object DirName {
  def apply(string: String): Either[NotDirectoryException, DirName] = {
    if string.isEmpty then Left(NotDirectoryException(s"folder name cannot be empty"))
    else if !os.isDir(os.pwd / string) then Left(NotDirectoryException(s"folder '${os.pwd / string}', does not exist"))
    else Right(new DirName(string))
  }
}