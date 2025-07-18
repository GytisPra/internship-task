package com.internshiptask.Config

import java.io.File

case class ScoptConfig(
    locationsFile: Option[File] = None,
    regionsFile: Option[File] = None,
    outputFile: Option[File] = None
)
