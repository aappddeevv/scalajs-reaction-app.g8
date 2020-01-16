val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.0.0-RC2")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
