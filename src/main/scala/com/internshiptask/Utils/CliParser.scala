package com.internshiptask.Utils

object CliParser {
  import com.internshiptask.Config.ScoptConfig
  import scopt.OParser
  import java.io.File

  def parse(args: Seq[String]): ScoptConfig =
    val builder = OParser.builder[ScoptConfig]

    val parser1 = {
      import builder._
      OParser.sequence(
        programName("internship-task"),
        head("internship-task", "1.0.0"),
        opt[File]("locations")
          .required()
          .valueName("<file>")
          .action((file, c) => c.copy(locationsFile = file))
          .validate(file =>
            if file.exists && file.isFile then success
            else failure(s"$file does not exist or is not a file")
          )
          .text("Path to the locations JSON file"),
        opt[File]("regions")
          .required()
          .valueName("<file>")
          .action((file, c) => c.copy(regionsFile = file))
          .validate(file =>
            if file.exists && file.isFile then success
            else failure(s"$file does not exist or is not a file")
          )
          .text("Path to the regions JSON file"),
        opt[File]("output")
          .required()
          .valueName("<file>")
          .action((file, c) => c.copy(outputFile = file))
          .text("Path to where to output results")
      )
    }

    OParser.parse(parser1, args, ScoptConfig()) match
        case None => sys.exit(1)
        case Some(config) => config 
}
