import sbt.Keys.scalaVersion

lazy val root = (project in file("."))
    .enablePlugins(SunriseThemeImporterPlugin)
    .settings(
      version := "0.1",
      scalaVersion := "2.11.8"
    )
