# Adds support for call-by-need parameters

This is an attempt (read as experiment) to add [by-need](https://en.wikipedia.org/wiki/Evaluation_strategy#Call_by_need) arguments in Scala. It is implemented using scalameta macro annotations.

## Example

```
import com.tudorzgureanu.lazyargs._

@WithLazy
def lazyFoo(cond: Boolean)(@Lazy bar: String) = if (cond) bar + bar else ""
```

## Installation

Clone the repo and publish the project locally:

```
> git clone git@github.com:tudorzgureanu/scala-lazy-arguments.git
> sbt publishLocal
```

In your build.sbt file:

```
lazy val enableMacroAnnotations: Seq[Def.Setting[_]] = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full),
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0" % Provided,
  scalacOptions += "-Xplugin-require:macroparadise"
)
```
and then add it to your project settings:
```
lazy val root =
  (project in file("."))
    .settings(
      enableMacroAnnotations,
      libraryDependencies ++= Seq(
        "com.tudorzgureanu" %% "scala-lazy-arguments" % "0.1.0-SNAPSHOT"       
      ))

```

## Why call-by-need and why call-by-name is not enough?

Scala provides support for by-need arguments (`def foo(bar: => String)`) but they are evaluated on every occurence in the code. Call-by-need is a memoised version of call-by-name (i.e. it is evaluated only once when it's used for the first time).

## Implementation details

The current implementation relies on local lazy val definitions so keep in mind when using it.

## Future plans

I will try to find time to follow the progress of scalameta and apply any improvements to this project. If you have any ideas feel free to open an issue.

