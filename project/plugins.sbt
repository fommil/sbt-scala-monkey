addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M13")

ivyLoggingLevel := UpdateLogging.Quiet

scalacOptions in Compile ++= Seq("-feature", "-deprecation")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
