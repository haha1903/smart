import sbt._
import Keys._

object SmartextBuild extends Build {
  val Organization = "com.baidu"
  val Name = "SmartExt"
  val Version = "1.0.0-SNAPSHOT"
  val ScalaVersion = "2.11.5"
  val ScalatraVersion = "2.3.6"
  val SlickVersion = "2.1.0"

  lazy val project = Project(
    "smartext",
    file("."),
    settings = Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      scalacOptions ++= Seq("-deprecation", "-feature"),
      resolvers ++= Seq(Resolver.url("nexus-ivy", url("http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/"))(Resolver.ivyStylePatterns),
        "nexus-m2" at "http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/"),
      // resolvers ++= Seq(Resolver.url("nexus-ivy", url("http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/"))(Resolver.ivyStylePatterns),
      //      "nexus-m2" at "http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/",
      //      Classpaths.typesafeReleases),
      externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false),
      //      publishTo := {
      //        val nexus = "http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/repositories/"
      //        if (isSnapshot.value)
      //          Some("snapshots" at nexus + "baidu-snapshot")
      //        else
      //          Some("releases" at nexus + "baidu")
      //      },
      publishTo := Some("ma03" at "http://cq01-testing-ma03.vm.baidu.com:8304/archiva/repository/ma/"),
      credentials += Credentials("Repository Archiva Managed ma Repository", "cq01-testing-ma03.vm.baidu.com", "admin", "!@34QWer"),
      //      credentials += Credentials("Sonatype Nexus Repository Manager", "cq01-rdqa-pool106.cq01.baidu.com", "admin", "!@34QWer"),
      libraryDependencies ++= Seq(
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-commands" % ScalatraVersion,
        "org.scalatra" %% "scalatra-swagger" % ScalatraVersion,
        "com.typesafe.slick" %% "slick" % SlickVersion,
        "com.typesafe" % "config" % "1.2.1",
        "org.clapper" %% "grizzled-slf4j" % "1.0.2",
        "org.json4s" %% "json4s-jackson" % "3.2.10",
        "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"
      )
    )
  )
}
