package com.tudorzgureanu.lazyargs

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta.Type.Arg
import scala.meta._
import scala.meta.contrib._
import scala.reflect.ClassTag

class WithLazy extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        val updatedParamss = WithLazy.lazyParamssToByName(defn.paramss)
        val lazyAnnotatedParams = WithLazy.collectLazyParams(defn.paramss)
        val localLazyValDefinitions: Seq[Defn.Val] = WithLazy.createLazyValDefinitions(lazyAnnotatedParams)
        val innerDefn = defn.copy(name = Term.Name(WithLazy.addInnerSuffix(defn.name.value)), paramss = updatedParamss)
        val replaceWith = lazyAnnotatedParams.map(param => param.name.value -> Term.Name(WithLazy.addLazySuffix(param.name.value))).toMap
        val argss = WithLazy.replaceArgs(replaceWith, WithLazy.toArgss(updatedParamss))
        val updatedBody =
          q"""
              {
              $innerDefn
              ..$localLazyValDefinitions
              ${innerDefn.name}(...$argss)
              }
           """
        defn.copy(paramss = updatedParamss, body = updatedBody)
      case other => abort(other.pos, "@WithLazy must annotate a method.")
    }
  }
}

object WithLazy {
  private val lazySuffix = "$Lazy"
  private val innerSuffix = "$Inner"

  def collectLazyParams(paramss: Seq[Seq[Term.Param]]): Seq[Term.Param] = {
    paramss.flatMap { paramList =>
      paramList.filter(isLazy)
    }
  }

  def lazyParamssToByName(paramss: Seq[Seq[Term.Param]]): Seq[Seq[Term.Param]] =
    paramss.map { params =>
      params.map {
        case lazyParam@Term.Param(_, _, Some(tpe: Type.Name), _) if WithLazy.isLazy(lazyParam) =>
          lazyParam.copy(decltpe = Some(Arg.ByName(tpe)))
        case other => other
      }
    }

  def isLazy(param: Term.Param): Boolean = param.mods.exists(modMatchesAnnot[Lazy])

  def createLazyValDefinitions(lazyAnnotatedParams: Seq[Term.Param]): Seq[Defn.Val] =
    lazyAnnotatedParams.map {
      case param@Term.Param(_, name, Some(tpe: Type.Name), _) =>
        val lazyTerm = Pat.Var.Term(Term.Name(addLazySuffix(name.value)))
        q"lazy val $lazyTerm: $tpe = ${param.name.asTerm}"
      case param@Term.Param(_, _, Some(_: Arg.Repeated), _) =>
        abort(param.pos, "@Lazy varargs not supported yet.")
      case other =>
        abort(other.pos, "No @Lazy parameter allowed here.")
    }

  def addInnerSuffix(name: String): String = name + innerSuffix

  def addLazySuffix(name: String): String = name + lazySuffix

  def toArgss(paramss: Seq[Seq[Term.Param]]): Seq[Seq[Term.Name]] = paramss.map(_.map(param => param.name.asTerm))

  def replaceArgs(replaceWith: Map[String, Term.Name], argss: Seq[Seq[Term.Name]]): Seq[Seq[Term.Name]] = {
    argss.map { args =>
      args.map {
        case Term.Name(value) if replaceWith.isDefinedAt(value) => replaceWith(value)
        case other => other
      }
    }
  }

  /**
    * Generates all the subpackages for the given class.
    *
    * e.g.
    * allSubPackages[com.tudorzgureanu.lazyargs.Lazy] yields
    * Vector(
    *   _root_.com.tudorzgureanu.lazyargs.Lazy,
    *   com.tudorzgureanu.lazyargs.Lazy,
    *   tudorzgureanu.lazyargs.Lazy,
    *   lazyargs.Lazy,
    *   Lazy)
    */
  private def allSubPackages[T](implicit classTag: ClassTag[T]): Seq[String] = {
    val subPackages = ("_root_." + classTag.runtimeClass.getName).split("\\.")
    for {
      i <- 0 until subPackages.size
    } yield subPackages.drop(i).mkString(".")
  }

  /**
    * Returns true if `mod` is an annotation and matches the the FQCN of `@Annot`.
    */
  private def modMatchesAnnot[Annot <: StaticAnnotation: ClassTag](mod: Mod) = mod match {
    case annot: Mod.Annot => allSubPackages[Annot].contains(annot.body.syntax)
    case _ => false
  }
}
