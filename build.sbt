
sbtPlugin := true

organization := "com.github.sbtliquibase"

name := "sbt-liquibase-plugin"

version := "0.1.0-SNAPSHOT"

crossScalaVersions := Seq("2.10.4")

libraryDependencies += "org.liquibase" % "liquibase-core" % "3.3.0"

publishMavenStyle := true

scriptedSettings

//scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

// TODO publish to Sonatype OSS
