package com.internshiptask.Utils

object CliParser {
  import com.internshiptask.Config.ScoptConfig
  import scopt.{OParser, DefaultOEffectSetup}
  import com.typesafe.scalalogging.Logger
  import java.io.File

  def parse(args: Seq[String])(using logger: Logger): Either[String, ScoptConfig] =
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
            if file.exists && file.isFile && file.canRead then success
            else failure(s"${file.getPath} either does not exist, can't be read or is not a file")
          )
          .text("Path to the locations JSON"),
        opt[File]("regions")
          .required()
          .valueName("<file>")
          .action((file, c) => c.copy(regionsFile = file))
          .validate(file =>
            if file.exists && file.isFile && file.canRead then success
            else failure(s"${file.getPath} either does not exist, can't be read or is not a file")
          )
          .text("Path to the regions JSON file"),
        opt[File]("output")
          .required()
          .valueName("<file>")
          .action((file, c) => c.copy(outputFile = file))
          .validate(file =>
            if !file.exists && !file.createNewFile then failure(s"Couldn't create ${file.getPath}")
            else if !file.canWrite then failure(s"Cannot write to ${file.getPath}")
            else if !file.isFile then failure(s"${file.getPath} is not a file")
            else success
          )
          .text("Path to where to output results. Will overwrite the file if it exists.")
      )
    }

    OParser.runParser(parser1, args, ScoptConfig()) match
      case (results, effects) =>
        OParser.runEffects(
          effects,
          new DefaultOEffectSetup {
            override def reportError(msg: String): Unit                   = logger.error(msg)
            override def reportWarning(msg: String): Unit                 = logger.warn(msg)
            // ignore terminate
            override def terminate(exitState: Either[String, Unit]): Unit = ()
          }
        )
        results match
          case None        => Left(s"Failed to parse args")
          case Some(value) => Right(value)

}
