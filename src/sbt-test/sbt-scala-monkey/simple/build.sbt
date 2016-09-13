scalaVersion in ThisBuild := "2.11.8"

val monkeys = Project("monkeys", file("project/monkeys")).settings(
  libraryDependencies ++= List(
    "org.scala-lang" % "scala-compiler" % scalaVersion.value
  ),
  scalaInstance <<= Defaults.scalaInstanceTask // or we get recursion
)

scalaMonkeyJar := (packageBin in Compile in LocalProject("monkeys")).value
