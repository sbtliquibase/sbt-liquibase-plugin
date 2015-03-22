SBT Liquibase plugin for sbt 0.13.7+
====================================

# Code Climate
[![Build Status](https://travis-ci.org/sbtliquibase/sbt-liquibase-plugin.svg)](https://travis-ci.org/sbtliquibase/sbt-liquibase-plugin)

# Instructions for use:
### Step 1: Include the plugin in your build

Add the following to your `project/plugins.sbt`:

## sbt-0.13.7+

    resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

    addSbtPlugin("com.github.sbtliquibase" % "sbt-liquibase" % "0.1.0-SNAPSHOT")

### Step 2: Activate sbt-liquibase-plugin in your build

Add the following to your 'build.sbt' ( if you are using build.sbt )


    import com.github.sbtliquibase.SbtLiquibase
    
    enablePlugins(SbtLiquibase)
    
    liquibaseUsername := ""

    liquibasePassword := ""

    liquibaseDriver   := "com.mysql.jdbc.Driver"

    liquibaseUrl      := "jdbc:mysql://localhost:3306/test_db?createDatabaseIfNotExist=true"

Or if you are using a build object extending from Build:

    TODO

## Settings

<table>
        <tr>
                <td> <b>liquibaseUsername</b> </td>
                <td>Username for the database. This defaults to blank.</td>
        </tr>
        <tr><td></td><td>

            liquibaseUsername := "your_db_username"

        </td></tr>
        <tr>
                <td> <b>liquibasePassword</b> </td>
                <td>Password for the database. This defaults to blank.</td>
        </tr>
        <tr><td></td><td>

            liquibasePassword := "secret"

        </td></tr>
        <tr>
                <td> <b>liqubaseDriver</b> </td>
                <td>Database driver classname. There is no default.</td>
        </tr>
        <tr><td></td><td>

            liquibaseDriver := "com.mysql.jdbc.Driver"

        </td></tr>
        <tr>
                <td> <b>liquibaseUrl</b> </td>
                <td>Database connection uri. There is no default.</td>
        </tr>
        <tr><td></td><td>

            liquibaseUrl := "jdbc:mysql://localhost:3306/mydb"

        </td></tr>
        <tr>
                <td> <b>liquibaseChangelogCatalog</b> </td>
                <td>Default catalog name for the changelog tables. This defaults to None.</td>
        </tr>
        <tr><td></td><td>

            liquibaseChangelogCatalog := Some("my_catalog")

        </td></tr>
        <tr>
                <td> <b>liquibaseChangelogSchemaName</b> </td>
                <td>Default schema name for the changelog tables. This defaults to None.</td>
        </tr>
        <tr><td></td><td>

            liquibaseChangelogSchemaName := Some("my_schema")

        </td></tr>
        <tr>
                <td> <b>liquibaseDefaultCatalog</b> </td>
                <td>Default catalog name for the db if it isn't defined in the uri. This defaults to None.</td>
        </tr>
        <tr><td></td><td>

            liquibaseDefaultCatalog := Some("my_catalog")

        </td></tr>
        <tr>
                <td> <b>liquibaseDefaultSchemaName</b> </td>
                <td>Default schema name for the db if it isn't defined in the uri. This defaults to None.</td>
        </tr>
        <tr><td></td><td>

            liquibaseDefaultSchemaName := Some("my_schema")

        </td></tr>
        <tr>
                <td> <b>liquibaseChangelog</b> </td>
                <td>Full path to your changelog file. This defaults 'src/main/migrations/changelog.xml'.</td>
        </tr>
        <tr><td></td><td>

            liquibaseChangelog := "other/path/dbchanges.xml"

        </td></tr>
</table>

## Tasks

<table>
        <tr>
                <td> <b>liquibase-update</b> </td>
                <td>Run the liquibase migration</td>
        </tr>
        <tr>
                <td><b>liquibase-status</b></td>
                <td>Print count of yet to be run changesets</td>
        </tr>
        <tr>
                <td><b>liquibase-clear-checksums</b></td>
                <td>Removes all saved checksums from database log. Useful for 'MD5Sum Check Failed' errors</td>
        </tr>
        <tr>
                <td><b>liquibase-list-locks</b></td>
                <td>Lists who currently has locks on the database changelog</td>
        </tr>
        <tr>
                <td><b>liquibase-release-locks</b></td>
                <td>Releases all locks on the database changelog.</td>
        </tr>
        <tr>
                <td><b>liquibase-validate-changelog</b></td>
                <td>Checks changelog for errors.</td>
        </tr>
        <tr>
                <td><b>liquibase-db-diff</b></td>
                <td>( this isn't implemented yet ) Generate changeSet(s) to make Test DB match Development</td>
        </tr>
        <tr>
                <td><b>liquibase-db-doc</b></td>
                <td>Generates Javadoc-like documentation based on current database and change log</td>
        </tr>
        <tr>
                <td><b>liquibase-generate-changelog</b></td>
                <td>Writes Change Log XML to copy the current state of the database to the file defined in the changelog setting</td>
        </tr>
        <tr>
                <td><b>liquibase-changelog-sync-sql</b></td>
                <td>Writes SQL to mark all changes as executed in the database to STDOUT</td>
        </tr>

        <tr>
                <td><b>liquibase-tag</b> {tag}</td>
                <td>Tags the current database state for future rollback with {tag}</td>
        </tr>
        <tr>
                <td><b>liquibase-rollback</b> {tag}</td>
                <td>Rolls back the database to the the state is was when the {tag} was applied.</td>
        </tr>
        <tr>
                <td><b>liquibase-rollback-sql</b> {tag}</td>
                <td>Writes SQL to roll back the database to that state it was in when the {tag} was applied to STDOUT</td>
        </tr>
        <tr>
                <td><b>liquibase-rollback-count</b> {int}</td>
                <td>Rolls back the last {int i} change sets applied to the database</td>
        </tr>
        <tr>
                <td><b>liquibase-rollback-sql-count</b> {int}</td>
                <td>Writes SQL to roll back the last {int i} change sets to STDOUT applied to the database</td>
        </tr>

        <tr>
                <td><b>liquibase-rollback-to-date</b> { yyyy-MM-dd HH:mm:ss }</td>
                <td>Rolls back the database to the the state it was at the given date/time. Date Format: yyyy-MM-dd HH:mm:ss</td>
        </tr>
        <tr>
                <td><b>liquibase-rollback-to-date-sql { yyyy-MM-dd HH:mm:ss }</b></td>
                <td>Writes SQL to roll back the database to that state it was in at the given date/time version to STDOUT</td>
        </tr>
        <tr>
                <td><b>liquibase-future-rollback-sql</b></td>
                <td>Writes SQL to roll back the database to the current state after the changes in the changelog have been applied.</td>
        </tr>
	<tr>
		<td><b>liquibase-drop-all</b></td>
		<td>Drop all tables</td>
	</tr>

</table>


Notes
------------------
After happily using Liquibase for sometime with Gradle builds I wanted to make use of Liquibase in my SBT projects as well.  Doing some searching I found this excelent SBT plugin https://github.com/bigtoast/sbt-liquibase. Unfortunatly I immediatly ran into several issues around test scoped dependencies which prompted me to refresh this project and also update it to 0.13.7 plugin standards.

The plugin works as is for me but it hasn't been as throughly tested as I would like.  To address this I am working on adding scripted tests and CI builds.  Additionally I would like to add missing Liquibase tasks such as diff and get this published to Sonatype OSS and eventually Maven Central.

Please file bugs or feature requests and I will do my best to address them.

Acknoledgements
---------------
Inspiration from previous work done by others on the following projects was an enourmous help.
 * sbt-liquibase plugin for sbt 0.11/0.12 (thanks for actually making this plugin in the furst place!)
 * sbt-web (helped to bring this up to speed with 0.13 plugin standars)
 * sbt-assembly (learned a lot about scripted sbt tests)



