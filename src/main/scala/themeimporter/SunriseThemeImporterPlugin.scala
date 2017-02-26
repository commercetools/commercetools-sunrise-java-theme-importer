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

    val sunriseThemeImportAll: TaskKey[Unit] =
      taskKey[Unit]("Imports all files into the project to enable editing")


    lazy val baseSunriseThemeImporterSettings: Seq[Def.Setting[_]] = Seq(

      sunriseThemeJarName := "commercetools-sunrise-theme",

      sunriseThemeImportTemplateFiles := {
        val fileNames = spaceDelimited("<arg>").parsed.map("templates/" + _)
        val themeJar = sunriseThemeJar.value
        val resourceDir = (resourceDirectory in Compile).value
        val logger = streams.value.log
        copyToResources(fileNames, themeJar, resourceDir, logger)
      },

      sunriseThemeImportI18nFiles := {
        val fileNames = spaceDelimited("<arg>").parsed.map("i18n/" + _)
        val themeJar = sunriseThemeJar.value
        val resourceDir = (resourceDirectory in Compile).value
        val logger = streams.value.log
        copyToResources(fileNames, themeJar, resourceDir, logger)
      },

      sunriseThemeImportAll := {
        val logger = streams.value.log
        val themeJar = sunriseThemeJar.value

        val cssDir = baseDirectory.value / "public" / "stylesheets"
        copyDirContentToResources("css", themeJar, cssDir, logger)

        val jsDir = baseDirectory.value / "public" / "javascripts"
        copyDirContentToResources("js", themeJar, jsDir, logger)

        val imgDir = baseDirectory.value / "public" / "images"
        copyDirContentToResources("img", themeJar, imgDir, logger)

        val fontsDir = baseDirectory.value / "public" / "fonts"
        copyDirContentToResources("fonts", themeJar, fontsDir, logger)

        val resourceDir = (resourceDirectory in Compile).value
        copyDirContentToResources("i18n", themeJar, resourceDir / "i18n", logger)
        copyDirContentToResources("templates", themeJar, resourceDir / "templates", logger)
      }
    )
  }

  import autoImport._

  val sunriseThemeJar: Def.Initialize[Task[Attributed[File]]] = Def.taskDyn {
    (dependencyClasspath in Compile).map(_.find(_.data.name.startsWith(sunriseThemeJarName.value)) match {
      case None => sys.error("No JAR file in classpath with a name starting with \"" + sunriseThemeJarName.value + "\"")
      case Some(file) =>
        streams.value.log.debug("Found matching JAR file \"" + file.data.name + "\"")
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

  private def copyDirContentToResources(dirname: String, themeJar: Attributed[File], resourceDir: File, logger: Logger) = {
    IO.withTemporaryDirectory { webjarTmpFile =>
      IO.unzip(themeJar.data, webjarTmpFile)
      copyDirContent(webjarTmpFile, resourceDir, dirname, logger)
    }
  }

  private def copyFile(webjarFile: File, resourceFolder: File, filename: String, logger: Logger) = {
    val sourceFile = webjarFile / WEBJARS_PATH / filename
    val targetFile = resourceFolder / filename
    if (!sourceFile.exists) {
      logger.warn("Ignored file \"" + filename + "\", it could not be found")
    } else if (targetFile.exists) {
      logger.warn("Skipping file, \"" + filename + "\" already exists in destination folder " + resourceFolder.name)
    } else {
      logger.debug("Copying file from \"" + sourceFile + "\" to \"" + targetFile + "\"")
      IO.copyFile(sourceFile, targetFile)
      logger.success("Successfully imported file \"" + filename + "\" into folder \"" + resourceFolder.name + "\"")
    }
  }

  private def copyDirContent(webjarFile: File, resourceFolder: File, dirname: String, logger: Logger) = {
    val sourceFile = webjarFile / WEBJARS_PATH / dirname
    val targetFile = resourceFolder
    if (!sourceFile.exists) {
      logger.warn("Ignored folder \"" + dirname + "\", it could not be found")
    } else {
      logger.debug("Copying folder content from \"" + sourceFile + "\" to \"" + targetFile + "\"")
      IO.copyDirectory(sourceFile, targetFile)
      logger.success("Successfully imported folder content from \"" + dirname + "\" into folder \"" + resourceFolder.name + "\"")
    }
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(Compile)(baseSunriseThemeImporterSettings)
}
