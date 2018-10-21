import scala.sys.process._

lazy val buildSettings = Seq(
  organization := "$org$",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  scalaVersion := "2.12.4",
  //resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayRepo("aappddeevv", "maven"),  
)

val commonScalacOptions = Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:_",
    "-unchecked",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Xfuture",
    "-Ypartial-unification",
)

val scalajsReactVersion = "0.1.0-M7"

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions ++
        (if (scalaJSVersion.startsWith("0.6."))
      Seq("-P:scalajs:sjsDefinedByDefault")
        else Nil),
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  libraryDependencies ++= Seq(
    "ttg" %%% "scalajs-react-core" % scalajsReactVersion,
    "ttg" %%% "scalajs-react-fabric" % scalajsReactVersion,
    "ttg" %%% "scalajs-react-react-dom" % scalajsReactVersion,
    "org.scala-js" %%% "scalajs-dom" % "latest-version",
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
