SBT Liquibase plugin for sbt 0.13.7+
====================================

# Code Climate
[![Build Status](https://travis-ci.org/sbtliquibase/sbt-liquibase-plugin.svg)](https://travis-ci.org/sbtliquibase/sbt-liquibase-plugin)

# Instructions for use:
### Step 1: Include the plugin in your build

Add the following to your `project/plugins.sbt`:

## sbt-0.13.7+

    addSbtPlugin("com.github.sbtliquibase" % "sbt-liquibase" % "0.2.0")

### Step 2: Activate sbt-liquibase-plugin in your build

Add the following to your 'build.sbt' ( if you are using build.sbt )


    import com.github.sbtliquibase.SbtLiquibase
    
    enablePlugins(SbtLiquibase)
    
    liquibaseUsername := ""

    liquibasePassword := ""

    liquibaseDriver   := "com.mysql.jdbc.Driver"

    liquibaseUrl      := "jdbc:mysql://localhost:3306/test_db?createDatabaseIfNotExist=true"

## Settings

|Setting|Description|Example|
|-------|-----------|-------|
|liquibaseUsername|Username for the database. This defaults to blank.|`liquibaseUsername := "your_db_username"`|
|liquibasePassword|Password for the database. This defaults to blank.|`liquibasePassword := "secret"`|
|liqubaseDriver|Database driver classname. There is no default.|`liquibaseDriver := "com.mysql.jdbc.Driver"`|
|liquibaseUrl|Database connection uri. There is no default.|`liquibaseUrl := "jdbc:mysql://localhost:3306/mydb"`|
|liquibaseChangelogCatalog|Default catalog name for the changelog tables. This defaults to None.|`liquibaseChangelogCatalog := Some("my_catalog")`|
|liquibaseChangelogSchemaName|Default schema name for the changelog tables. This defaults to None.|`liquibaseChangelogSchemaName := Some("my_schema")`|
|liquibaseDefaultCatalog|Default catalog name for the db if it isn't defined in the uri. This defaults to None.|`liquibaseDefaultCatalog := Some("my_catalog")`|
|liquibaseDefaultSchemaName|Default schema name for the db if it isn't defined in the uri. This defaults to None.|`liquibaseDefaultSchemaName := Some("my_schema")`|
|liquibaseChangelog|Full path to your changelog file. This defaults 'src/main/migrations/changelog.xml'.|`liquibaseChangelog := "other/path/dbchanges.xml"`|

## Tasks

|Task|Description|
|----|-----------|
|`liquibase-update`|Run the liquibase migration|
|`liquibase-status`|Print count of yet to be run changesets|
|`liquibase-clear-checksums`|Removes all saved checksums from database log. Useful for 'MD5Sum Check Failed' errors|
|`liquibase-list-locks`|Lists who currently has locks on the database changelog|
|`liquibase-release-locks`|Releases all locks on the database changelog.|
|`liquibase-validate-changelog`|Checks changelog for errors.|
|`liquibase-db-diff`|( this isn't implemented yet ) Generate changeSet(s) to make Test DB match Development|
|`liquibase-db-doc`|Generates Javadoc-like documentation based on current database and change log|
|`liquibase-generate-changelog`|Writes Change Log XML to copy the current state of the database to the file defined in the changelog setting|
|`liquibase-changelog-sync-sql`|Writes SQL to mark all changes as executed in the database to STDOUT|
|`liquibase-tag {tag}`|Tags the current database state for future rollback with {tag}|
|`liquibase-rollback {tag}`|Rolls back the database to the the state is was when the {tag} was applied.|
|`liquibase-rollback-sql {tag}`|Writes SQL to roll back the database to that state it was in when the {tag} was applied to STDOUT|
|`liquibase-rollback-count {int}`|Rolls back the last {int i} change sets applied to the database|
|`liquibase-rollback-count-sql {int}`|Writes SQL to roll back the last {int i} change sets to STDOUT applied to the database|
|`liquibase-rollback-to-date {yyyy-MM-dd HH:mm:ss}`|Rolls back the database to the the state it was at the given date/time. Date Format: yyyy-MM-dd HH:mm:ss|
|`liquibase-rollback-to-date-sql { yyyy-MM-dd HH:mm:ss }`|Writes SQL to roll back the database to that state it was in at the given date/time version to STDOUT|
|`liquibase-future-rollback-sql`|Writes SQL to roll back the database to the current state after the changes in the changelog have been applied.|
|`liquibase-drop-all`|Drop all tables|

Notes
------------------
After happily using Liquibase for sometime with Gradle builds I wanted to make use of Liquibase in my SBT projects as well.  Doing some searching I found this excelent SBT plugin https://github.com/bigtoast/sbt-liquibase. Unfortunatly I immediatly ran into several issues around test scoped dependencies which prompted me to refresh this project and also update it to 0.13.7 plugin standards.

The plugin works as is for me but it hasn't been as throughly tested as I would like.  To address this I am working on adding scripted tests and CI builds.  Additionally I would like to add missing Liquibase tasks such as diff and get this published to Sonatype OSS and eventually Maven Central.

Please file bugs or feature requests and I will do my best to address them.

Acknoledgements
---------------
Inspiration from previous work done by others on the following projects was an enourmous help.
 * sbt-liquibase plugin for sbt 0.11/0.12 (thanks for actually making this plugin in the first place!)
 * sbt-web (helped to bring this up to speed with 0.13 plugin standards)
 * sbt-assembly (learned a lot about scripted sbt tests)



