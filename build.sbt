import Dependencies._


lazy val libDependencies = Seq(
  scalaMeta % Compile,
  scalaMetaContrib % Compile,
  // testing
  scalaTest % Test
)

lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
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

lazy val root = (project in file(".")).
  settings(
    name := "scala-lazy-arguments",
    description := "Call by need arguments in Scala",
    inThisBuild(List(
      organization := "com.tudorzgureanu",
      scalaVersion := "2.12.2",
      version := "0.1.0-SNAPSHOT"
    )),
    libraryDependencies ++= libDependencies,
    metaMacroSettings,
    licenses := ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")) :: Nil
  ).enablePlugins(ScalafmtPlugin)
