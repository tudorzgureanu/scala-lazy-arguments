# Adds support of call-by-need parameters

At this point it's more like experimenting with scalameta rather than a real thing, although it might change at some point.


TODOs:
- [x] Initial Implementation of @WithLazy which makes all the args lazy.
- [x] Add support for @Lazy annotated args.
- [ ] Add additional configuration parameters to @WithLazy (e.g. strategy = one of [All, None, LazyAnnotated])
- [ ] [In progress] Unit testing.
