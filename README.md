# Adds support of call-by-need parameters

At this point it's more like experimenting with scalameta rather than a real thing, although it might change at some point.

This is an attempt of adding by-need arguments in Scala. It comes with a macro annotation implemented using scalameta.

## Example

```
@WithLazy
def lazyFoo(cond: Boolean)(@Lazy bar: String) = if (cond) bar + bar else ""

```

TODOs:
- [x] Initial Implementation of @WithLazy which makes all the args lazy.
- [x] Add support for @Lazy annotated args.
- [ ] Add additional configuration parameters to @WithLazy (e.g. strategy = one of [All, None, LazyAnnotated])
- [ ] [In progress] Unit testing.
- [ ] Update README.md with a short project overview.
