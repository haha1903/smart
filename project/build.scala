import sbt._
import sbt.Keys._

object build extends Build {
  val Organization = "com.baidu"
  val Version = "1.1.2-SNAPSHOT"
  val ScalaVersion = "2.11.5"
  val ScalatraVersion = "2.3.6"
  val SlickVersion = "2.1.0"

  lazy val smart = Project(
    id = "smart",
    base = file("."),
    settings = Seq(publish := {}, publishLocal := {}),
    aggregate = Seq(smartCore, smartExt)
  )

  lazy val smartCore = Project(
    "smart-core",
    file("smart-core"),
    settings = generalSetting ++ Seq(
      name := "smart-core",
      libraryDependencies ++= Seq(
        "com.typesafe" % "config" % "1.2.1",
        "org.clapper" %% "grizzled-slf4j" % "1.0.2",
        "org.json4s" %% "json4s-jackson" % "3.2.10"
      )
    )
  )

  lazy val smartExt = Project(
    "smart-ext",
    file("smart-ext"),
    settings = generalSetting ++ Seq(
      name := "smart-ext",
      libraryDependencies ++= Seq(
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-commands" % ScalatraVersion,
        "org.scalatra" %% "scalatra-swagger" % ScalatraVersion,
        "com.typesafe.slick" %% "slick" % SlickVersion,
        "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"
      )
    )
  ).dependsOn(smartCore)

  lazy val kick = Project(
    "kick",
    file("kick"),
    settings = generalSetting ++ Seq(
      name := "kick",
      libraryDependencies ++= Seq(
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        "net.debasishg" %% "redisclient" % "2.13"
      )
    )
  ).dependsOn(smartCore)

  val generalSetting = Seq(
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq("-deprecation", "-feature"),
    resolvers ++= Seq("ma03" at "http://cq01-testing-ma03.vm.baidu.com:8304/archiva/repository/ma/",
      "nexus-m2" at "http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/",
      Resolver.url("nexus-ivy", url("http://cq01-rdqa-pool106.cq01.baidu.com:8081/nexus/content/groups/public/"))(Resolver.ivyStylePatterns),
      Classpaths.typesafeReleases),
    externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false),
    publishTo := Some("ma03" at "http://cq01-testing-ma03.vm.baidu.com:8304/archiva/repository/ma/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".ma03_credentials")
  )
}
