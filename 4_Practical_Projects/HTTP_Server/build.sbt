name := "scala-rest-api-server"

version := "1.0.0"

scalaVersion := "2.13.8"

// HTTP4s for web server
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % "0.23.16",
  "org.http4s" %% "http4s-blaze-server" % "0.23.16",
  "org.http4s" %% "http4s-blaze-client" % "0.23.16",
  "org.http4s" %% "http4s-circe" % "0.23.16",

  // Circe for JSON
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",

  // PureConfig for configuration
  "com.github.pureconfig" %% "pureconfig" % "0.17.4",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.4.11",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",

  // Testing
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.http4s" %% "http4s-testing" % "0.23.16" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test,

  // Cats Effect
  "org.typelevel" %% "cats-effect" % "3.5.1"
)

// Assembly configuration for fat JAR
assembly / assemblyJarName := "rest-api-server.jar"
assembly / mainClass := Some("restapiserver.ApiServer")
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}
