import sbt._

name := "MonitorDatiGov"

version := "1.0"

scalaVersion := "2.10.6"


val assemblyName = "monitorDatiGov"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0"  //% "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.0" //% "provided"
libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector_2.10" % "1.1.0"



assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


/*
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Xfuture"
)


resolvers ++= Seq(
  Resolver.mavenLocal
)

/**
  * unless Spark and hadoop get in  trouble about signed jars.
  */
val hadoopHBaseExcludes =
  (moduleId: ModuleID) => moduleId.
    excludeAll(ExclusionRule(organization = "org.mortbay.jetty")).
    excludeAll(ExclusionRule(organization = "org.eclipse.jetty")).
    excludeAll(ExclusionRule(organization = "javax.servlet"))

val sparkVersion = "1.6.0"

/**
  * when used inside the IDE they are imported with scope "compile",
  * Otherwise when submitted with spark_submit they are  "provided"
  */
// provided
def providedOrCompileDependencies(scope: String = "compile"): Seq[ModuleID] = Seq(
  hadoopHBaseExcludes("org.apache.spark" %% "spark-core" % sparkVersion % scope),
  hadoopHBaseExcludes("org.apache.spark" %% "spark-sql"% sparkVersion % scope)
)

val commonDependencies = Seq(

  "com.typesafe" % "config" % "1.0.2",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test")



lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= commonDependencies ++ providedOrCompileDependencies()
  ).enablePlugins(AssemblyPlugin, JavaAppPackaging)

lazy val projectAssembly = (project in file("assembly")).
  settings(
    mainClass in assembly := Some("it.teamDigitale.kafkaProducers.SimpleProducer"),
    assemblyJarName in assembly := s"$assemblyName-${version.value}.jar"
  ) dependsOn root

scriptClasspath ++= Seq(s"$assemblyName-${version.value}.jar")

*/