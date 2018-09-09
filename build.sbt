
sbtPlugin := true

organization := "com.github.sbtliquibase"

name := "sbt-liquibase"

version := "0.3.0-SNAPSHOT"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/sbtliquibase/sbt-liquibase-plugin"))

crossScalaVersions := Seq("2.10.4")

libraryDependencies += "org.liquibase" % "liquibase-core" % "3.5.3"

publishMavenStyle := true

scriptedSettings

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

// TODO publish to Sonatype OSS
