package com.internshiptask.Config

import java.io.File

case class ScoptConfig(
    locationsFile: File = File("."),
    regionsFile: File = File("."),
    outputFile: File = File(".")
)