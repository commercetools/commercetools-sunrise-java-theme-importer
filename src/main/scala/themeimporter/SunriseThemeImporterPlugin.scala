package themeimporter

import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

object SunriseThemeImporterPlugin extends AutoPlugin {

  private val WEBJARS_PATH = "META-INF/resources/webjars/"

  object autoImport {

    val sunriseThemeJarName: SettingKey[String] = settingKey[String]("Name of the JAR where to find the Sunrise Theme")

    val sunriseThemeImportTemplateFiles: InputKey[Unit] =
      inputKey[Unit]("Imports the given template files into the project to enable editing, e.g.: 'sunriseThemeImportTemplateFiles common/logo.hbs cart.hbs'")

    val sunriseThemeImportI18nFiles: InputKey[Unit] =
      inputKey[Unit]("Imports the given i18n files into the project to enable editing, e.g.: 'sunriseThemeImportI18nFiles en/home.yaml de/home.yaml'")


    lazy val baseSunriseThemeImporterSettings: Seq[Def.Setting[_]] = Seq(
      sunriseThemeJarName := "commercetools-sunrise-theme",

      sunriseThemeImportTemplateFiles := {
        val fileNames = spaceDelimited("<arg>").parsed.map("templates/" + _)
        val resourceDir = (resourceDirectory in Compile).value
        val themeJar = sunriseThemeJar.value
        val logger = streams.value.log
        copyToResources(fileNames, themeJar, resourceDir, logger)
      },

      sunriseThemeImportI18nFiles := {
        val fileNames = spaceDelimited("<arg>").parsed.map("i18n/" + _)
        val resourceDir = (resourceDirectory in Compile).value
        val themeJar = sunriseThemeJar.value
        val logger = streams.value.log
        copyToResources(fileNames, themeJar, resourceDir, logger)
      }
    )
  }

  import autoImport._

  val sunriseThemeJar: Def.Initialize[Task[Attributed[File]]] = Def.taskDyn {
    (dependencyClasspath in Compile).map(_.find(_.data.name.startsWith(sunriseThemeJarName.value)) match {
      case None => sys.error("No JAR file in classpath with a name starting with " + sunriseThemeJarName.value)
      case Some(file) =>
        streams.value.log.debug("Found matching JAR file " + file.data.name)
        file
    })
  }

  private def copyToResources(filenames: Seq[String], themeJar: Attributed[File], resourceDir: File, logger: Logger) = {
    IO.withTemporaryDirectory { webjarTmpFile =>
      IO.unzip(themeJar.data, webjarTmpFile)
      filenames.foreach(filename => {
        copyFile(webjarTmpFile, resourceDir, filename, logger)
      })
    }
  }

  private def copyFile(webjarFile: File, resourceFolder: File, filename: String, logger: Logger) = {
    val sourceFile = webjarFile / WEBJARS_PATH / filename
    val targetFile = resourceFolder / filename
    if (!sourceFile.exists) {
      logger.warn("File " + filename + " could not be found in " + sourceFile)
    } else if (targetFile.exists) {
      logger.warn("Skipping file, " + filename + " already exists in destination folder " + resourceFolder.name)
    } else {
      logger.debug("Copying file from " + sourceFile + " to " + targetFile)
      IO.copyFile(sourceFile, targetFile)
      logger.success("Successfully imported file " + filename + " into folder " + resourceFolder.name)
    }
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(Compile)(baseSunriseThemeImporterSettings)
}
