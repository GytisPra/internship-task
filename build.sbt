val scala3Version = "3.7.1"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "internship-task",
    organization := "com.internshiptask",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalactic"    %% "scalactic" % "3.2.19",
      "org.scalatest"    %% "scalatest" % "3.2.19" % Test,
      "com.lihaoyi"      %% "upickle"   % "4.1.0",
      "com.lihaoyi"      %% "os-lib"    % "0.11.4",
      "com.github.scopt" %% "scopt"     % "4.1.0"
    )
  )
