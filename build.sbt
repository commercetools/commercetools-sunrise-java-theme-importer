sbtPlugin := true

name := "commercetools-sunrise-theme-importer"

organization in ThisBuild := "com.commercetools.sunrise"

scalaVersion in ThisBuild := "2.10.6"

Release.publishSettings

Release.enableSignedRelease

ScriptedPlugin.scriptedBufferLog := false

ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

ScriptedPlugin.scriptedLaunchOpts ++= Seq(
  "-Dplugin.version=" + version.value
)