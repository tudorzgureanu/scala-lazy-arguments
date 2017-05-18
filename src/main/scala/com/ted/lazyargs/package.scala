package com.ted

import scala.annotation.StaticAnnotation
import scala.meta._
import scala.reflect.ClassTag

package object lazyargs {

  /**
    * Checks if `maybeSuffix` is a suffix of `fullName`.
    *
    * e.g.:
    * isSuffix(c, a.b.c) // true
    * isSuffix(b.c, a.b.c) // true
    * isSuffix(a.b.c, a.b.c) // true
    * isSuffix(_root_.a.b.c, a.b.c) // true
    * isSuffix(d.c, a.b.c) // false
    */
  private[this] def isSuffix(maybeSuffix: Term, fullName: Term): Boolean =
    (maybeSuffix, fullName) match {
      case (a: Name, b: Name) => a.value == b.value
      case (Select(q"_root_", a), b: Name) => a.value == b.value
      case (a: Name, Select(_, b)) => a.value == b.value
      case (Select(aRest, a), Select(bRest, b)) =>
        a.value == b.value && isSuffix(aRest, bRest)
      case _ => false
    }

  /**
    * Returns true if `mod` is an annotation and matches the the FQCN of `@Annot`.
    */
  private[lazyargs] def modMatchesAnnot[Annot <: StaticAnnotation : ClassTag](mod: Mod): Boolean =
    mod match {
      case Mod.Annot(term: Term.Ref) => isSuffix(term, termRefForType[Annot])
      case _ => false
    }

  /**
    * Parses `T.runtimeClass.getName` into a `Term.Ref`.
    * Uses runtime reflection, but this happens only at compile time.
    */
  private[this] def termRefForType[T](implicit ev: ClassTag[T]): Term.Ref =
    ev.runtimeClass.getName.parse[Term].get.asInstanceOf[Term.Ref]
}

// will be added to the next release: https://github.com/scalameta/scalameta/pull/800
object Select {
  def unapply(tree: Tree): Option[(Term, Name)] = tree match {
    case Term.Select(qual, name) => Some(qual -> name)
    case Type.Select(qual, name) => Some(qual -> name)
    case Ctor.Ref.Select(qual, name) => Some(qual -> name)
    case _ => None
  }
}
