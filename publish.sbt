
def settingsIfCI[T](setting: => T):Seq[T] = {
  for { isCi <- sys.env.get("CI") if isCi.toBoolean } yield setting
}.toSeq

def settingIfCI[T](setting: => T):Option[T] = {
  for { isCi <- sys.env.get("CI") if isCi.toBoolean } yield setting
}

credentials ++= settingsIfCI {
  Credentials(
    "Sonatype Nexus Repository Manager", "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USERNAME", ""),
    sys.env.getOrElse("SONATYPE_PASSWORD", ""))
}

pgpSecretRing := settingIfCI(file("local.secring.gpg")).getOrElse(pgpSecretRing.value)
pgpPublicRing := settingIfCI(file("local.pubring.gpg")).getOrElse(pgpPublicRing.value)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra :=
  <scm>
    <url>git@github.com:sbtliquibase/sbt-liquibase-plugin.git</url>
    <connection>scm:git:git@github.com:sbtliquibase/sbt-liquibase-plugin.git</connection>
  </scm>
  <developers>
    <developer>
      <id>lance</id>
      <name>Lance Linder</name>
      <url>http://buddho.io/lance</url>
    </developer>
  </developers>