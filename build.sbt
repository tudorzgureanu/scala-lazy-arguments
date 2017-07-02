import Dependencies._


lazy val libDependencies = Seq(
  scalaMeta % Compile,
  scalaMetaContrib % Compile,
  // testing
  scalaTest % Test
)

lazy val metaMacroSettings = Seq(
  // A dependency on macro paradise 3.x is required to both write and expand
  // new-style macros.  This is similar to how it works for old-style macro
  // annotations and a dependency on macro paradise 2.x.
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  // temporary workaround for https://github.com/scalameta/paradise/issues/10
  scalacOptions in(Compile, console) := Seq(), // macroparadise plugin doesn't work in repl yet.
  // temporary workaround for https://github.com/scalameta/paradise/issues/55
  sources in(Compile, doc) := Nil // macroparadise doesn't work with scaladoc yet.
)

lazy val bintraySettings = Seq(
  bintrayRepository := "generic",
  bintrayOrganization := Some("tudorzgureanu"),
  publishArtifact in Test := false,
  bintrayPackageLabels := Seq("scala", "lazy", "call-by-need"),
  bintrayVcsUrl := Some("https://github.com/tudorzgureanu/scala-lazy-arguments")
)

lazy val root = (project in file(".")).
  settings(
    name := "scala-lazy-arguments",
    description := "Call by need arguments in Scala",
    organization := "com.tudorzgureanu",
    scalaVersion := "2.12.2",
    version := "0.2.0-SNAPSHOT",
    crossScalaVersions := Seq("2.11.11", "2.12.2"),
    libraryDependencies ++= libDependencies,
    licenses := ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")) :: Nil,
    metaMacroSettings,
    publishArtifact in Test := false,
    publishMavenStyle := false,
    bintraySettings
  ).enablePlugins(ScalafmtPlugin)
