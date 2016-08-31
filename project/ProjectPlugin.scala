import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import de.heikoseeberger.sbtheader.HeaderKey
import sbt._
import sbt.Keys._
import scalariform.formatter.preferences._

object ProjectPlugin extends SensiblePlugin {

  override def projectSettings = Seq(
    ivyLoggingLevel := UpdateLogging.Quiet,
    HeaderKey.headers := Copyright.ApacheMap,
    ScalariformKeys.preferences := FormattingPreferences().setPreference(AlignSingleLineCaseStatements, true),
    libraryDependencies ++= Sensible.testLibs()
  ) ++ Sensible.settings ++ SonatypeSupport.sonatype("fommil", "sbt-scala-monkey", SonatypeSupport.Apache2)

}
