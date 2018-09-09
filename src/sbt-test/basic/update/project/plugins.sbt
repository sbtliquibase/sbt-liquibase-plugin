// build root project
//lazy val root = Project("plugins", file(".")) dependsOn sbtLiquibase

// depends on the sbtLiquibase project
//lazy val sbtLiquibase = file("../../../../").getAbsoluteFile.toURI

sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.github.sbtliquibase" % "sbt-liquibase" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.182",
  "org.liquibase" % "liquibase-core" % "3.5.3"
)
