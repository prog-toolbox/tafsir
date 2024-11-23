ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.2"

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "tafsir",
    scalaJSUseMainModuleInitializer := true
  )


libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"

// Scala.js Settings
scalacOptions += "-scalajs"

// Enable Source Maps for Debugging
scalaJSLinkerConfig ~= { _.withSourceMap(true) }

// Scala.js Output Directory
Compile / fastOptJS / artifactPath := baseDirectory.value / "target" / "web" / "main.js"
Compile / fullOptJS / artifactPath := baseDirectory.value / "docs" / "main.js"

// Additional configuration
Compile / fastOptJS / artifactPath := baseDirectory.value / "docs" / "main-dev.js"

// HTML File
Compile / resourceGenerators += Def.task {
  val indexHtml = baseDirectory.value / "docs" / "index.html"
  IO.write(indexHtml, """<!DOCTYPE html>
  <html>
  <head>
      <meta charset="UTF-8">
      <title>Tafsir</title>
  </head>
  <body>
      <script src="main.js"></script>
  </body>
  </html>
  """)
  Seq(indexHtml)
}.taskValue