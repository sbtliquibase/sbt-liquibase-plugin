
sbtPlugin := true

organization := "com.github.sbtliquibase"

name := "sbt-liquibase"

version := "0.1.0-SNAPSHOT"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/sbtliquibase/sbt-liquibase-plugin"))

crossScalaVersions := Seq("2.10.4")

libraryDependencies += "org.liquibase" % "liquibase-core" % "3.3.1"

publishMavenStyle := true

scriptedSettings

//scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

// TODO publish to Sonatype OSS
