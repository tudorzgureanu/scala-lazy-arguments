package com.ted.`lazy`

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta.Mod.Annot
import scala.meta.Type.Arg.ByName
import scala.meta._
import scala.meta.contrib._

class WithLazy extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        println(defn.syntax)

        val udpatedParamss = defn.paramss.map { params =>
          params.map {
            case lazyParam@Term.Param(_, _, Some(tpe: Type.Name), _) if WithLazy.isLazy(lazyParam) =>
              lazyParam.copy(decltpe = Some(ByName(tpe)))
            case other => other
          }
        }

        val lazyAnnotatedParams = WithLazy.collectLazyArgs(defn.paramss)

        println(lazyAnnotatedParams)

        val localLazyVals: Seq[Defn.Val] = lazyAnnotatedParams.map {
          case param@Term.Param(_, name, Some(tpe: Type.Name), _) =>
            val lazyTerm = Pat.Var.Term(Term.Name(WithLazy.addLazySuffix(name.value)))
            q"lazy val $lazyTerm: $tpe = ${param.name.asTerm}"
        }

        val innerDefn = defn.copy(name = Term.Name(WithLazy.addInnerSuffix(defn.name.value)), paramss = udpatedParamss)
        val replaceWith = lazyAnnotatedParams.map(param => param.name.value -> Term.Name(WithLazy.addLazySuffix(param.name.value))).toMap
        val args = WithLazy.replaceArgs(replaceWith, WithLazy.toArgss(udpatedParamss))
        val updatedBody =
          q"""
              {
              $innerDefn

              ..$localLazyVals

              ${innerDefn.name}(...$args)
              }
           """

        val updatedDefn = defn.copy(paramss = udpatedParamss, body = updatedBody)

        println(s"Generated definition for ${defn.name}:")
        println(updatedDefn.syntax)

        updatedDefn

      case other => abort(other.pos, "@WithLazy must annotate a method.")
    }
  }
}

object WithLazy {
  private val lazySuffix = "$Lazy"
  private val innerSuffix = "$Inner"

  def collectLazyArgs(paramss: Seq[Seq[Term.Param]]): Seq[Term.Param] = {
    paramss.flatMap { paramList =>
      paramList.filter(isLazy)
    }
  }

  def isLazy(param: Term.Param): Boolean = {
    println(param.mods.collect {
      case a@Annot(term) =>
        println(a)
        println(term)
        println("_______")
    })
    //TODO check if the parameter is annotated with @Lazy. Need to consider also @com.ted.`lazy`.Lazy, @ted.`lazy`.Lazy etc.
    true
  }

  def addInnerSuffix(name: String): String = name + innerSuffix
  def addLazySuffix(name: String): String = name + lazySuffix

  def toArgss(params: Seq[Seq[Term.Param]]): Seq[Seq[Term.Name]] = params.map(_.map(param => param.name.asTerm))

  def replaceArgs(replaceWith: Map[String, Term.Name], argss: Seq[Seq[Term.Name]]): Seq[Seq[Term.Name]] = {
    argss.map{ args =>
      args.map {
        case Term.Name(value) if replaceWith.isDefinedAt(value) => replaceWith(value)
        case other => other
      }
    }
  }

}

// todo check if only the function arguments are annotated and also check if the enclosing function is annotated with `@WithLazy`
class Lazy extends StaticAnnotation
