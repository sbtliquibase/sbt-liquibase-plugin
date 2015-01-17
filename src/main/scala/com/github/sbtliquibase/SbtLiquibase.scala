package com.github.sbtliquibase

import java.io.{OutputStreamWriter, PrintStream}
import java.text.SimpleDateFormat

import liquibase.Liquibase
import liquibase.database.Database
import liquibase.diff.output.DiffOutputControl
import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.FileSystemResourceAccessor
import sbt.Keys._
import sbt.Scoped._
import sbt.{Setting, _}
import sbt.classpath._
import sbt.complete.DefaultParsers._


object Import {
  val liquibaseUpdate = TaskKey[Unit]("liquibase-update", "Run a liquibase migration")
  val liquibaseStatus = TaskKey[Unit]("liquibase-status", "Print count of unrun change sets")
  val liquibaseClearChecksums = TaskKey[Unit]("liquibase-clear-checksums", "Removes all saved checksums from database log. Useful for 'MD5Sum Check Failed' errors")
  val liquibaseListLocks = TaskKey[Unit]("liquibase-list-locks", "Lists who currently has locks on the database changelog")
  val liquibaseReleaseLocks = TaskKey[Unit]("liquibase-release-locks", "Releases all locks on the database changelog")
  val liquibaseValidateChangelog = TaskKey[Unit]("liquibase-validate-changelog", "Checks changelog for errors")
  val liquibaseTag = InputKey[Unit]("liquibase-tag", "Tags the current database state for future rollback")
  val liquibaseDbDiff = TaskKey[Unit]("liquibase-db-diff", "( this isn't implemented yet ) Generate changeSet(s) to make Test DB match Development")
  val liquibaseDbDoc = TaskKey[Unit]("liquibase-db-doc", "Generates Javadoc-like documentation based on current database and change log")
  val liquibaseGenerateChangelog = TaskKey[Unit]("liquibase-generate-changelog", "Writes Change Log XML to copy the current state of the database to standard out")
  val liquibaseChangelogSyncSql = TaskKey[Unit]("liquibase-changelog-sync-sql", "Writes SQL to mark all changes as executed in the database to STDOUT")
  val liquibaseDropAll = TaskKey[Unit]("liquibase-drop-all", "Drop all database objects owned by user")

  val liquibaseRollback = InputKey[Unit]("liquibase-rollback", "<tag> Rolls back the database to the the state is was when the tag was applied")
  val liquibaseRollbackSql = InputKey[Unit]("liquibase-rollback-sql", "<tag> Writes SQL to roll back the database to that state it was in when the tag was applied to STDOUT")
  val liquibaseRollbackCount = InputKey[Unit]("liquibase-rollback-count", "<num>Rolls back the last <num> change sets applied to the database")
  val liquibaseRollbackCountSql = InputKey[Unit]("liquibase-rollback-count-sql", "<num> Writes SQL to roll back the last <num> change sets to STDOUT applied to the database")
  val liquibaseRollbackToDate = InputKey[Unit]("liquibase-rollback-to-date", "<date> Rolls back the database to the the state is was at the given date/time. Date Format: yyyy-MM-dd HH:mm:ss")
  val liquibaseRollbackToDateSql = InputKey[Unit]("liquibase-rollback-to-date-sql", "<date> Writes SQL to roll back the database to that state it was in at the given date/time version to STDOUT")
  val liquibaseFutureRollbackSql = InputKey[Unit]("liquibase-future-rollback-sql", " Writes SQL to roll back the database to the current state after the changes in the changelog have been applied")

  val liquibaseDataDir = SettingKey[File]("liquibase-data-dir", "This is the liquibase migrations directory.")
  val liquibaseChangelog = SettingKey[File]("liquibase-changelog", "This is your liquibase changelog file to run.")
  val liquibaseUrl = SettingKey[String]("liquibase-url", "The url for liquibase")
  val liquibaseUsername = SettingKey[String]("liquibase-username", "username yo.")
  val liquibasePassword = SettingKey[String]("liquibase-password", "password")
  val liquibaseDriver = SettingKey[String]("liquibase-driver", "driver")
  val liquibaseDefaultCatalog = SettingKey[Option[String]]("liquibase-default-catalog", "default catalog")
  val liquibaseDefaultSchemaName = SettingKey[Option[String]]("liquibase-default-schema-name", "default schema name")
  val liquibaseChangelogCatalog = SettingKey[Option[String]]("liquibase-changelog-catalog", "changelog catalog")
  val liquibaseChangelogSchemaName = SettingKey[Option[String]]("liquibase-changelog-schema-name", "changelog schema name")
  val liquibaseContext = SettingKey[String]("liquibase-context", "changeSet contexts to execute")
  val liquibaseOutputDefaultCatalog = SettingKey[Boolean]("liquibase-output-default-catalog", "Whether to ignore the schema name.")
  val liquibaseOutputDefaultSchema = SettingKey[Boolean]("liquibase-output-default-schema", "Whether to ignore the schema name.")

  lazy val liquibaseDatabase = TaskKey[Database]("liquibase-database", "the database")
  lazy val liquibaseInstance = TaskKey[Liquibase]("liquibase", "liquibase object")
}

object SbtLiquibase extends AutoPlugin {

  import Import._

  lazy val DateParser = mapOrFail(StringBasic) { date =>
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
  }

  class RichLiquibase(liquibase: Liquibase) {
    def execAndClose(f: (Liquibase) => Unit): Unit = {
      try { f(liquibase) } finally { liquibase.getDatabase.close() }
    }
  }
  implicit def RichLiquibase(liquibase: Liquibase): RichLiquibase = new RichLiquibase(liquibase)

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val autoImport = Import

  override def projectSettings: Seq[Setting[_]] = liquibaseBaseSettings(Compile) ++ inConfig(Test)(liquibaseBaseSettings(Test))

  def liquibaseBaseSettings(conf: Configuration): Seq[Setting[_]] = {

    Seq[Setting[_]](
      liquibaseDefaultCatalog := None,
      liquibaseDefaultSchemaName := None,
      liquibaseChangelogCatalog := None,
      liquibaseChangelogSchemaName := None,
      liquibaseDataDir := baseDirectory.value / "src" / "main" / "migrations",
      liquibaseChangelog := liquibaseDataDir.value / "changelog.xml",
      liquibaseContext := "",
      liquibaseOutputDefaultCatalog := true,
      liquibaseOutputDefaultSchema := true,

      liquibaseDatabase <<= database(conf),

      liquibaseGenerateChangelog <<= generateChangeLog,

      liquibaseInstance := new Liquibase(liquibaseChangelog.value.absolutePath, new FileSystemResourceAccessor, liquibaseDatabase.value),

      liquibaseUpdate := liquibaseInstance.value.execAndClose(_.update(liquibaseContext.value)),

      liquibaseStatus := liquibaseInstance.value.execAndClose {
        _.reportStatus(true, liquibaseContext.value, new OutputStreamWriter(System.out))
      },

      liquibaseClearChecksums := liquibaseInstance.value.execAndClose(_.clearCheckSums()),

      liquibaseListLocks := liquibaseInstance.value.execAndClose(_.reportLocks(new PrintStream(System.out))),

      liquibaseReleaseLocks <<= (streams, liquibaseInstance) map { (out, lbase) => lbase.execAndClose { _.forceReleaseLocks() } },

      liquibaseValidateChangelog <<= (streams, liquibaseInstance) map { (out, lbase) => lbase.validate()},

      liquibaseDbDoc := {
        val path = (target.value / "liquibase-doc").absolutePath
        liquibaseInstance.value.execAndClose(_.generateDocumentation(path))
        streams.value.log.info(s"Documentation generated in $path")
      },

      liquibaseRollback := {
        val tag = token(Space ~> StringBasic, "<tag>").parsed
        liquibaseInstance.value.execAndClose(_.rollback(tag, liquibaseContext.value))
        streams.value.log.info("Rolled back to tag %s".format(tag))
      },

      liquibaseRollbackCount := {
        val count = token(Space ~> IntBasic, "<count>").parsed
        liquibaseInstance.value.execAndClose(_.rollback(count, liquibaseContext.value))
        streams.value.log.info("Rolled back to count %s".format(count))
      },

      liquibaseRollbackSql := {
        val tag = token(Space ~> StringBasic, "<tag>").parsed
        liquibaseInstance.value.execAndClose {
          _.rollback(tag, liquibaseContext.value, new OutputStreamWriter(System.out))
        }
      },

      liquibaseRollbackCountSql := {
        val count = token(Space ~> IntBasic, "<count>").parsed
        liquibaseInstance.value.execAndClose {
          _.rollback(count, liquibaseContext.value, new OutputStreamWriter(System.out))
        }
      },

      liquibaseRollbackToDate := {
        val date = token(Space ~> DateParser, "<date/time>").parsed
        liquibaseInstance.value.execAndClose(_.rollback(date, liquibaseContext.value))
      },

      liquibaseRollbackToDateSql := {
        val date = token(Space ~> DateParser, "<date/time>").parsed
        liquibaseInstance.value.execAndClose {
          _.rollback(date, liquibaseContext.value, new OutputStreamWriter(System.out))
        }
      },

      liquibaseFutureRollbackSql := liquibaseInstance.value.execAndClose {
        _.futureRollbackSQL(liquibaseContext.value, new OutputStreamWriter(System.out))
      },

      liquibaseTag := {
        val tag = token(Space ~> StringBasic, "<tag>").parsed
        liquibaseInstance.value.execAndClose(_.tag(tag))
        streams.value.log.info(s"Tagged db with $tag for future rollback if needed")
      },

      liquibaseChangelogSyncSql := liquibaseInstance.value.execAndClose {
        _.changeLogSync(liquibaseContext.value, new OutputStreamWriter(System.out))
      },

      liquibaseDropAll := liquibaseInstance.value.execAndClose(_.dropAll())
    )
  }

  def generateChangeLog = {
    ( streams, liquibaseInstance, liquibaseChangelog, liquibaseDefaultCatalog, liquibaseDefaultSchemaName,
      liquibaseChangelogCatalog, liquibaseChangelogSchemaName,
      liquibaseOutputDefaultCatalog, liquibaseOutputDefaultSchema, liquibaseDataDir) map {
      (out, lbase, clog, defaultCatalog, defaultSchemaName,
       liquibaseChangelogCatalog, liquibaseChangelogSchemaName,
       liquibaseOutputDefaultCatalog, liquibaseOutputDefaultSchema, dataDir) =>
      CommandLineUtils.doGenerateChangeLog(
        clog.absolutePath,
        lbase.getDatabase,
        defaultCatalog.orNull,
        defaultSchemaName.orNull,
        null, // snapshotTypes
        null, // author
        null, // context
        dataDir.absolutePath,
        new DiffOutputControl(liquibaseOutputDefaultCatalog, liquibaseOutputDefaultSchema, true))
    }
  }

  def database(conf: Configuration) = {

    // Extract values from the setting keys
    (liquibaseUrl, liquibaseUsername, liquibasePassword,
      liquibaseDriver, liquibaseDefaultCatalog, liquibaseDefaultSchemaName,
      liquibaseChangelogCatalog, liquibaseChangelogSchemaName, dependencyClasspath in conf) map {
      (url, uname, pass, driver, liquibaseDefaultCatalog,
       liquibaseDefaultSchemaName, liquibaseChangelogCatalog,
       liquibaseChangelogSchemaName, cpath) =>

        // create new Database intance
        CommandLineUtils.createDatabaseObject(
          ClasspathUtilities.toLoader(cpath.map(_.data)),
          url,
          uname,
          pass,
          driver,
          liquibaseDefaultCatalog.orNull,
          liquibaseDefaultSchemaName.orNull,
          false, // outputDefaultCatalog
          true, // outputDefaultSchema
          null, // databaseClass
          null, // driverPropertiesFile
          null, // propertyProviderClass
          liquibaseChangelogCatalog.orNull,
          liquibaseChangelogSchemaName.orNull
        )
    }
  }
}
