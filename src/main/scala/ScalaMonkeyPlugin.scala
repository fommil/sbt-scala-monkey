// Copyright: 2016 Sam Halliday
// License: http://www.apache.org/licenses/LICENSE-2.0
// Base works: see LICENSE-sbt
import sbt._
import Keys._

/**
 * This plugin allows builds to provide monkey patches to the scala compiler.
 */
object ScalaMonkeyPlugin extends AutoPlugin {
  object Keys {
    val scalaMonkeyJar = TaskKey[Option[File]]("Jar or classdirectory containing monkey patches to scala-compiler or scala-library.")
  }
  import Keys._

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  val autoImport = Keys

  override lazy val projectSettings = Seq(
    scalaInstance <<= scalaInstanceTask,
    scalaMonkeyJar := None
  )

  // copied from sbt/main/src/main/scala/sbt/Defaults.scala
  def scalaInstanceTask: Def.Initialize[Task[ScalaInstance]] = Def.taskDyn {
    scalaHome.value match {
      case Some(h) => Defaults.scalaInstanceFromHome(h)
      case None =>
        val scalaProvider = appConfiguration.value.provider.scalaProvider
        val version = scalaVersion.value
        if (version == scalaProvider.version) // use the same class loader as the Scala classes used by sbt
          Def.task(ScalaInstance(version, scalaProvider))
        else
          scalaInstanceFromUpdate
    }
  }

  // copied from sbt/main/src/main/scala/sbt/Defaults.scala
  def scalaInstanceFromUpdate: Def.Initialize[Task[ScalaInstance]] = Def.task {
    val toolReport = update.value.configuration(Configurations.ScalaTool.name) getOrElse
      sys.error(noToolConfiguration(managedScalaInstance.value))
    def files(id: String) =
      for {
        m <- toolReport.modules if m.module.name == id;
        (art, file) <- m.artifacts if art.`type` == Artifact.DefaultType
      } yield file
    def file(id: String) = files(id).headOption getOrElse sys.error(s"Missing ${id}.jar")
    val allFiles = toolReport.modules.flatMap(_.artifacts.map(_._2))
    val libraryJar = file(ScalaArtifacts.LibraryID)
    val compilerJar =
      if (ScalaInstance.isDotty(scalaVersion.value))
        file(ScalaArtifacts.dottyID(scalaBinaryVersion.value))
      else
        file(ScalaArtifacts.CompilerID)
    val otherJars = allFiles.filterNot(x => x == libraryJar || x == compilerJar)

    new ScalaInstance(
      scalaVersion.value,
      // monkeys first...
      makeClassLoader(state.value)(scalaMonkeyJar.value.toList ::: libraryJar :: compilerJar :: otherJars.toList),
      libraryJar,
      compilerJar,
      otherJars,
      None
    )
  }

  // copied from sbt/main/src/main/scala/sbt/Defaults.scala
  private[this] def noToolConfiguration(autoInstance: Boolean): String =
    {
      val pre = "Missing Scala tool configuration from the 'update' report.  "
      val post =
        if (autoInstance)
          "'scala-tool' is normally added automatically, so this may indicate a bug in sbt or you may be removing it from ivyConfigurations, for example."
        else
          "Explicitly define scalaInstance or scalaHome or include Scala dependencies in the 'scala-tool' configuration."
      pre + post
    }

  // copied from sbt/main/src/main/scala/sbt/Defaults.scala
  private[this] def makeClassLoader(state: State) = state.classLoaderCache.apply _

}
