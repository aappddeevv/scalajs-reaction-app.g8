import scala.sys.process._

// reload build.sbt on changes
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val buildSettings = Seq(
  organization := "$org$",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  scalaVersion := "2.13.1",
  //resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayRepo("aappddeevv", "maven"),  
)

val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Ywarn-numeric-widen",
  // 2.12 flags
  //"-Xfuture",
  //"-Yno-adapted-args",
  //"-Ypartial-unification",
)

val scalajsReactVersion = "0.1.0-M7"

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions ++
        (if (scalaJSVersion.startsWith("0.6."))
      Seq("-P:scalajs:sjsDefinedByDefault")
        else Nil),
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  libraryDependencies ++= Seq(
    "ttg" %%% "scalajs-reaction-core" % scalajsReactVersion,
    "ttg" %%% "scalajs-reaction-fabric" % scalajsReactVersion,
    "ttg" %%% "scalajs-reaction-react-dom" % scalajsReactVersion,
    "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    "org.scalatest"          %%% "scalatest"    % "latest.release" % "test")
)

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
  (fullOptJS in Compile).value
  "npm run app" !
}

val npmBuildFast = taskKey[Unit]("fastOptJS then webpack")
npmBuildFast := {
  (fastOptJS in Compile).value
  "npm run app:dev" !
}

val npmRunDemo = taskKey[Unit]("fastOptJS then run webpack server")
npmRunDemo := {
  (fastOptJS in Compile).value
  "npm run app:dev-start" !
}
