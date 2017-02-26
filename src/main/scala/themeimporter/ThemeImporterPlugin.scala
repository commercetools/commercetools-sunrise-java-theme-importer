package themeimporter

import sbt.Keys._
import sbt._
import sbt.complete.DefaultParsers.spaceDelimited

object ThemeImporterPlugin extends AutoPlugin {

  private val WEBJARS_PATH = "META-INF/resources/webjars/"

  object autoImport {

    val importTemplateFiles: InputKey[Unit] =
      inputKey[Unit]("Copies the provided template files into the project to enable editing, e.g.: 'importTemplateFiles common/logo.hbs cart.hbs'")

    val importI18nFiles: InputKey[Unit] =
      inputKey[Unit]("Copies the provided i18n files into the project to enable editing, e.g.: 'importI18nFiles en/home.yaml de/home.yaml'")

    val themeJarName: SettingKey[String] = settingKey[String]("Name of the JAR where to find the theme")

    lazy val themeImporterSettings: Seq[Def.Setting[_]] = Seq(

      importTemplateFiles := {
        val files = spaceDelimited("<arg>").parsed.map("templates/" + _)
        val destFolder = (resourceDirectory in Compile).value
        val dependencies = (dependencyClasspath in Compile).value
        copyFilesFrom(files, destFolder, dependencies, themeJarName.value, streams.value.log)
      },

      importI18nFiles := {
        val filesToCopy = spaceDelimited("<arg>").parsed.map("i18n/" + _)
        val destFolder = (resourceDirectory in Compile).value
        val dependencies = (dependencyClasspath in Compile).value
        copyFilesFrom(filesToCopy, destFolder, dependencies, themeJarName.value, streams.value.log)
      },

      themeJarName := "commercetools-sunrise-theme"
    )

    private def copyFilesFrom(filesToCopy: Seq[String], destFolder: File, dependencies: Seq[Attributed[File]], themeJarName: String, logger: Logger) = {
      dependencies.find(_.data.name.startsWith(themeJarName)) match {
        case Some(webjar) => executeFromJar(webjar, copyFile(filesToCopy, destFolder, logger))
        case None => logger.error("No file in classpath with name starting with " + themeJarName)
      }
    }

    private def executeFromJar(webjar: Attributed[File], action: File => Unit) = {
      IO.withTemporaryDirectory { tmpDir =>
        IO.unzip(webjar.data, tmpDir)
        action.apply(tmpDir / WEBJARS_PATH)
      }
    }

    private def copyFile(resourceNames: Seq[String], destFolder: File, logger: Logger): File => Unit = sourceFolder => {
      resourceNames
        .filter(fileName => {
          val exists = sourceFolder.toPath.resolve(fileName).toFile.exists
          if (!exists) logger.warn("Could not find file " + fileName)
          exists
        })
        .foreach(resourceName => {
          val sourceFile = sourceFolder / resourceName
          val targetFile = destFolder / resourceName
          if (targetFile.exists) {
            logger.warn("Skipping file, " + resourceName + " already exists in destination folder " + destFolder.name)
          } else {
            logger.debug("Copying file from " + sourceFile + " to " + targetFile)
            IO.copyFile(sourceFile, targetFile)
            logger.success("Successfully imported file " + resourceName + " into folder " + destFolder.name)
          }
        })
    }
  }

  import autoImport._

  override val projectSettings: Seq[Def.Setting[_]] = inConfig(Compile)(themeImporterSettings)
}