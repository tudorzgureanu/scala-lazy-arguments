# Adds support for call-by-need parameters

> At this point it's more like experimenting with scalameta rather than a real thing, although it might change at some point.

This is an attempt to add by-need arguments in Scala. It is implemented using scalameta macro annotations.

## Example

```
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
        "com.ted" %% "scala-lazy-arguments" % "0.1.0-SNAPSHOT"       
      ))

```

## Why call-by-need and why call-by-name is not enough?

Scala provides support for by-need arguments (`def foo(bar: => String)`) but they are evaluated on every occurence in the code. Call-by-need is a memoised version of call-by-name (i.e. it is evaluated only once when it's used for the first time).

## Implementation details
TBD

TODOs:
- [x] Initial Implementation of @WithLazy.
- [x] Add support for @Lazy annotated args.
- [ ] [In progress] Unit testing.
- [ ] Update README.md with a short project overview.
