ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.2"

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(name := "tafsir", scalaJSUseMainModuleInitializer := true)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "2.8.0",
  "org.typelevel" %%% "cats-core" % "2.12.0",
  "org.typelevel" %%% "cats-effect" % "3.5.2",
  "com.softwaremill.sttp.client3" %%% "cats" % "3.9.0",
  "com.softwaremill.sttp.client3" %%% "circe" % "3.9.0",
  "io.circe" %%% "circe-core" % "0.14.5",
  "io.circe" %%% "circe-generic" % "0.14.5",
  "io.circe" %%% "circe-parser" % "0.14.5",
  "com.lihaoyi" %%% "scalatags" % "0.13.1",
  "org.scalatest" %%% "scalatest" % "3.2.19" % Test
)

dependencyOverrides += "org.portable-scala" %%% "portable-scala-reflect" % "1.1.2"

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
  IO.write(
    indexHtml,
    """<!DOCTYPE html>
  <html>
  <head>
      <meta charset="UTF-8">
      <title>Tafsir</title>
     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
 </head>
  <body>
      <div id="app" class="container mt-5"></div>
      <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
      <script src="main.js"></script>
  </body>
  </html>
  """
  )
  Seq(indexHtml)
}.taskValue
