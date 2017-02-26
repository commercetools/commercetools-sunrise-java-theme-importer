import sbt.Keys.{resolvers, scalaVersion}
import sbt.Resolver

lazy val root = (project in file("."))
    .enablePlugins(SunriseThemeImporterPlugin)
    .settings(
      version := "0.1",
      scalaVersion := "2.11.8",
      resolvers += Resolver.bintrayRepo("commercetools", "maven"),
      libraryDependencies ++= Seq (
        "com.commercetools.sunrise" % "commercetools-sunrise-theme" % "0.64.0"
      )
    )
