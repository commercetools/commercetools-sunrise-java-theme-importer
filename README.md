# commercetools-sunrise-java-theme-importer
SBT plugin that allows to import parts of the theme into a Sunrise project

[![Build Status](https://travis-ci.org/commercetools/commercetools-sunrise-java-theme-importer.png?branch=master)](https://travis-ci.org/commercetools/commercetools-sunrise-java-theme-importer) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.commercetools.sunrise/sbt-commercetools-sunrise-theme-importer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.commercetools.sunrise/sbt-commercetools-sunrise-theme-importer)

## Installation
Add this plugin to your project in `project/plugins.sbt`:
```
addSbtPlugin("com.commercetools.sunrise" % "sbt-commercetools-sunrise-theme-importer" % "0.1.0")
```

Then enable it in your root project's settings:
```
val root = (project in file("."))
  .enablePlugins(SunriseThemeImporterPlugin)
```

## SBT Commands
In SBT you can use the following commands:
- `sunriseThemeImportTemplateFiles`: Imports the given template files into the project's resource folder to enable editing.

  E.g.:
  ```
  > sunriseThemeImportTemplateFiles common/logo.hbs cart.hbs
  ```
- `sunriseThemeImportI18nFiles`: Imports the given i18n files into the project's resource folder to enable editing.

  E.g.:
  ```
  > sunriseThemeImportI18nFiles en/home.yaml de/home.yaml
  ```
- `sunriseThemeImportAll`: Imports all Theme's files into the project to enable editing. For templates and i18n files, the destination is the project's resource folder. For the rest, the files go into a `public/` folder after Play Framework's structure.
