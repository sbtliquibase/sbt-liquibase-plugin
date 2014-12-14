import java.sql.{Connection, DriverManager}

import com.github.sbtliquibase.SbtLiquibase


lazy val test = (project in file("."))
  .enablePlugins(SbtLiquibase)
  .settings(
    scalaVersion := "2.11.4",
    name := "test-update",
    organization := "test",
    version := "0.1",

    liquibaseUsername := "",
    liquibasePassword := "",
    liquibaseDriver := "org.h2.Driver",
    liquibaseUrl := s"jdbc:h2:file:${target.value / "test"};INIT=CREATE SCHEMA IF NOT EXISTS TEST;",

    libraryDependencies ++= Seq(
      "com.h2database" % "h2" % "1.4.182"
    )
  )

val checkTablesTasks = TaskKey[Unit]("checkTables")

checkTablesTasks := {

  var connection: Connection = null
  try {
    println("Checking state of db after migration update.")
    Class.forName("org.h2.Driver")
    val url = s"jdbc:h2:file:${target.value / "test"};"
    connection = DriverManager.getConnection(url, "", "")
    val rs = connection.getMetaData.getTables(null, null, "LOCATION", null)
    if(!rs.next()) sys.error("expected LOCATION table to exist after Liquibase update")
  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    if(connection != null)
      connection.close()
  }
}


