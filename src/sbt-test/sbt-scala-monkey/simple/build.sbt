scalaVersion in ThisBuild := "2.11.8"

val monkeys = Project("monkeys", file("project/monkeys")).settings(
  libraryDependencies ++= List(
    "org.scala-lang" % "scala-compiler" % scalaVersion.value
  ),
  scalaInstance <<= Defaults.scalaInstanceTask // or we get recursion
)

scalaMonkeyJar <<= state.map { s =>
  val structure = Project.extract(s).structure
  val proj = structure.allProjectRefs.find(_.project == "monkeys").get
  EvaluateTask(structure, packageBin in Compile, s, proj) match {
    case Some((_, Value(v))) => Some(v)
    case _              => None
  }
}
