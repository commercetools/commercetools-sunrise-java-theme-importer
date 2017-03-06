# commercetools-sunrise-java-theme-importer

[![Build Status](https://travis-ci.org/commercetools/commercetools-sunrise-java-theme-importer.png?branch=master)](https://travis-ci.org/commercetools/commercetools-sunrise-java-theme-importer) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.commercetools.sunrise/sbt-commercetools-sunrise-theme-importer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.commercetools.sunrise/sbt-commercetools-sunrise-theme-importer)

SBT plugin that allows to import parts of the theme into a Sunrise project

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

### Import all files
Import all Theme's files into the project to enable editing:

```
> sunriseThemeImportAll
```
  
For templates and i18n files, the destination is the project's resource folder. For the rest, the files go into `public/` folder after [Play Framework's structure](https://www.playframework.com/documentation/2.5.x/Anatomy).

### Import individual files

#### Template files

Import the given template files into the project's resource folder to enable editing:

```
E.g.:
> sunriseThemeImportTemplateFiles common/logo.hbs cart.hbs
```

#### i18n files

Import the given i18n files into the project's resource folder to enable editing:

```
E.g.:
> sunriseThemeImportI18nFiles en/home.yaml de/home.yaml
```
