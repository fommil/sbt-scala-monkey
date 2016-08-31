sbtPlugin := true

organization := "com.fommil"
version := "1.0.0-SNAPSHOT"

scalacOptions in Compile ++= Seq(
  "-encoding", "UTF-8", "-target:jvm-1.7", "-feature", "-deprecation",
  "-Xfatal-warnings",
  "-language:postfixOps", "-language:implicitConversions"
)

ScriptedPlugin.scriptedSettings
scriptedLaunchOpts += "-Dplugin.version=" + version.value
scriptedBufferLog := false
