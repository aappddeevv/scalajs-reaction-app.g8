import scala.sys.process._

// reload build.sbt on changes
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val buildSettings = Seq(
  organization := "$org$",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  scalaVersion := "3.1.0"
)

val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Yexplicit-nulls",
  "-language:implicitConversions"
)

val scalajsReactVersion = "1.0.0-RC1"

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  libraryDependencies ++= Seq(
    "org.ttgoss.js" %%% "react" % scalajsReactVersion,
    "org.ttgoss.js" %%% "fabric" % scalajsReactVersion,
    "org.ttgoss.js" %%% "react-dom" % scalajsReactVersion,
    "org.scala-js" %%% "scalajs-dom" % "2.0.0",
))

lazy val libsettings = buildSettings ++ commonSettings

lazy val root = project.in(file("."))
  .settings(libsettings)
  // this name must be coordinated with scala.webpack.config.js
  .settings(name := "app")
  .enablePlugins(ScalaJSPlugin)


// Watch non-scala assets, when they change trigger sbt
// if you are using ~npmBuildFast, you get a rebuild
// when non-scala assets change
watchSources += baseDirectory.value / "src/main/js"
watchSources += baseDirectory.value / "src/main/public"

val npmBuild = taskKey[Unit]("fullOptJS then webpack")
npmBuild := {
  (Compile / fullOptJS).value
  "npm run app" !
}

val npmBuildFast = taskKey[Unit]("fastOptJS then webpack")
npmBuildFast := {
  (Compile / fastOptJS ).value
  "npm run app:dev" !
}

val npmRunDemo = taskKey[Unit]("fastOptJS then run webpack server")
npmRunDemo := {
  (Compile / fastOptJS).value
  "npm run app:dev-start" !
}
