import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import sbtbuildinfo.Plugin._
import scala.Some

object ApplicationBuild extends Build {

  val appName = "fastly-stats"
  val appVersion = "1.0"

  val appDependencies = Seq(
    "com.gu" %% "fastlyapiclient" % "0.3.0",
    "com.codahale.metrics" % "metrics-graphite" % "3.0.0"
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    jarName in assembly := "fastly-stats.jar",
    version := "0.1",
    organization := "com.gu",
    scalaVersion := "2.10.0"
  )

  val standardSettings = buildInfoSettings ++ Seq[Setting[_]](
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](
      libraryDependencies in Compile,
      name,
      version,
      BuildInfoKey.constant("buildNumber", Option(System.getenv("BUILD_NUMBER")) getOrElse "DEV"),
      // so this next one is constant to avoid it always recompiling on dev machines.
      // we only really care about build time on teamcity, when a constant based on when
      // it was loaded is just fine
      BuildInfoKey.constant("buildTime", System.currentTimeMillis)
    )
  )

  lazy val playArtifactDistSettings = assemblySettings ++ Seq(mainClass in assembly := Some("play.core.server.NettyServer"))

  val main = play.Project(appName, appVersion, appDependencies, settings = buildSettings ++ playArtifactDistSettings ++ standardSettings ++ artifactDistSettings)
    .settings(resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-releases")
    .settings(
    ivyXML :=
      <dependencies>
        <exclude org="commons-logging"/>
      </dependencies>,

    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case "play/core/server/ServerWithStop.class" => MergeStrategy.first
        case x => old(x)
      }
    }
  )

  /**
   * artifact creation for teamcity
   * all of this, just to create a bloody zip!
   */
  val artifactZip = TaskKey[File]("artifact-zip", "Builds a deployable zip file")

  lazy val artifactDistSettings = Seq(
    artifactZip <<= buildDeployArtifact
  )

  private def buildDeployArtifact = (target, assembly, baseDirectory) map {
    (target, assembly, baseDirectory) =>

      val resources = Seq(
        assembly -> "packages/%s/%s".format(appName, assembly.getName),
        baseDirectory / "deploy" / "deploy.json" -> "deploy.json"
      )

      val distFile = target / "artifacts.zip"

      if (distFile.exists()) distFile.delete()

      IO.zip(resources, distFile)

      // Tells TeamCity to publish the artifact => leave this println in here
      println("##teamcity[publishArtifacts '%s => .']" format distFile)

      distFile
  }

}
