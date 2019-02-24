val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.26")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
