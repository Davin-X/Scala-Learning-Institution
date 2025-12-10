name := "file-processor-cli"
version := "1.0.0"
scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  // Command line argument parsing
  "com.github.scopt" %% "scopt" % "4.1.0",

  // JSON processing
  "io.circle.circle" %% "circe-core" % "0.14.1",
  "io.circle.circle" %% "circe-generic" % "0.14.1",
  "io.circle.circle" %% "circe-parser" % "0.14.1",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",

  // Testing
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,

  // CSV processing
  "com.github.tototoshi" %% "scala-csv" % "1.3.10"
)

// Merge strategy for assembly
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
