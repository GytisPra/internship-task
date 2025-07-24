package com.internshiptask.Config

import java.io.File

case class ScoptConfig(
    locationsFile: File = new File(""),
    regionsFile: File = new File(""),
    outputFile: File = new File("")
)
