# Adds support for call-by-need parameters

At this point it's more like experimenting with scalameta rather than a real thing, although it might change at some point.

This is an attempt of adding by-need arguments in Scala. It comes with a macro annotation implemented using scalameta.

## Example

```
@WithLazy
def lazyFoo(cond: Boolean)(@Lazy bar: String) = if (cond) bar + bar else ""
```

## Why call-by-need and why call-by-name is not enough?

Scala provides support for by-need arguments (`def foo(bar: => String)`) but they are evaluated on every occurence in the code. Call-by-need is a memoised version of call-by-name (i.e. it is evaluated only once when it's used for the first time).

## Implementation details
TBD

TODOs:
- [x] Initial Implementation of @WithLazy which makes all the args lazy.
- [x] Add support for @Lazy annotated args.
- [ ] [In progress] Unit testing.
- [ ] Update README.md with a short project overview.
