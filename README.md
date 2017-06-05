# Adds support for call-by-need parameters

This is an attempt (read as experiment) to add [by-need](https://en.wikipedia.org/wiki/Evaluation_strategy#Call_by_need) arguments in Scala. It is implemented using scalameta macro annotations.

## Example

```
import com.tudorzgureanu.lazyargs._

@WithLazy
def lazyFoo(cond: Boolean)(@Lazy bar: String) = if (cond) bar + bar else ""
```

## Installation

You will need to use Scala `2.11.8+` or `2.12.x`.

- Add the bintray repo resolver for this project:
```
resolvers += Resolver.bintrayIvyRepo("tudorzgureanu", "generic")
```

- Add the dependency to this project:
```
libraryDependencies += "com.tudorzgureanu" %% "scala-lazy-arguments" % "0.1.0"
```

- Add scalameta paradise compiler plugin and scalameta dependency:
```
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full)

libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0" % Provided
```

You can find an example of a build.sbt file with all the required dependencies bellow:

```
scalaVersion := "2.12.2"

resolvers += Resolver.bintrayIvyRepo("tudorzgureanu", "generic")

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.tudorzgureanu" %% "scala-lazy-arguments" % "0.1.0",
  "org.scalameta" %% "scalameta" % "1.8.0" % Provided
)
```

## Why call-by-need and why call-by-name is not enough?

Scala provides support for by-need arguments (`def foo(bar: => String)`) but they are evaluated on every occurence in the code. Call-by-need is a memoised version of call-by-name (i.e. it is evaluated only once when it's used for the first time).

## Implementation details

The current implementation relies on local `lazy val`s so keep in mind when using it.

## Future plans

I will try to find time to follow the progress of scalameta and apply any improvements to this project. If you have any ideas feel free to open an issue.

Also, I plan to add a naive lazy implementation for the code that doesn't involve any multi-threading (which is most of the time). This will replace the local lazy vals with a simple (non thread-safe) cache.

