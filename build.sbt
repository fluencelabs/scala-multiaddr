import de.heikoseeberger.sbtheader.License
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbtcrossproject.crossProject

name := "scala-multiaddr"

scalacOptions in Compile ++= Seq("-Ypartial-unification", "-Xdisable-assertions")

javaOptions in Test ++= Seq("-ea")

skip in publish := true // Skip root project

val scalaV = scalaVersion := "2.12.5"

val commons = Seq(
  scalaV,
  version                   := "0.0.1",
  fork in Test              := true,
  parallelExecution in Test := false,
  organization              := "one.fluence",
  organizationName          := "Fluence Labs Limited",
  organizationHomepage      := Some(new URL("https://fluence.one")),
  startYear                 := Some(2017),
  licenses += ("AGPL-V3", new URL("http://www.gnu.org/licenses/agpl-3.0.en.html")),
  headerLicense       := Some(License.AGPLv3("2017", organizationName.value)),
  bintrayOrganization := Some("fluencelabs"),
  publishMavenStyle   := true,
  bintrayRepository   := "releases",
  resolvers += Resolver.bintrayRepo("fluencelabs", "releases")
)

commons

lazy val `scala-multiaddr-core` = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(FluenceCrossType)
  .in(file("core"))
  .settings(
    commons,
    libraryDependencies ++= Seq(
      "com.beachape" %%% "enumeratum" % "1.5.13",
      "org.scalatest" %%% "scalatest"   % "3.0.+" % Test
    )
  )
  .jsSettings(
    fork in Test := false
  )
  .enablePlugins(AutomateHeaderPlugin)

lazy val `scala-multiaddr-core-js` = `scala-multiaddr-core`.js
lazy val `scala-multiaddr-core-jvm` = `scala-multiaddr-core`.jvm