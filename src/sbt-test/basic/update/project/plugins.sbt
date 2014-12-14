// build root project
//lazy val root = Project("plugins", file(".")) dependsOn sbtLiquibase

// depends on the sbtLiquibase project
//lazy val sbtLiquibase = file("../../../../").getAbsoluteFile.toURI

{
  val pluginVersion = System.getProperty("plugin.version")

  if(pluginVersion == null) {
    throw new RuntimeException(
      """|The system property 'plugin.version' is not defined.
         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  }

  else addSbtPlugin("com.github.sbtliquibase" % "sbt-liquibase-plugin" % pluginVersion)
}

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.182",
  "org.liquibase" % "liquibase-core" % "3.3.0"
)