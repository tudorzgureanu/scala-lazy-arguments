package com.ted.`lazy`

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta.Type.Arg.ByName
import scala.meta._

class WithLazy extends StaticAnnotation {

  import WithLazy._

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>

        val udpatedParamss = defn.paramss.map { params =>
          params.map {
            case lazyParam@Term.Param(_, _, Some(tpe: Type.Name), _) if isLazy(lazyParam) => lazyParam.copy(decltpe = Some(ByName(tpe)))
            case other => other
          }
        }

        val lazyAnnotatedParams = collectLazyArgs(defn.paramss)
        val localLazyVals: Seq[Defn.Val] = lazyAnnotatedParams.map {
          case param@Term.Param(_, name, Some(tpe: Type.Name), _) =>
            val lazyTerm = Pat.Var.Term(Term.Name(name.syntax + "Lazy"))
            q"lazy val $lazyTerm: $tpe = ${Term.Name(param.name.value)}"
        }

        val updatedBody =
          q"""
              {
              ..$localLazyVals
              ${defn.body}
              }
           """
        // todo adjust all the expressions in the body to use the lazy versions of params
        defn.copy(paramss = udpatedParamss, body = updatedBody)

      case _ => abort("@WithLazy must annotate a method.")
    }
  }
}

object WithLazy {
  private def collectLazyArgs(paramss: Seq[Seq[Term.Param]]): Seq[Term.Param] = {
    paramss.flatMap { paramList =>
      paramList.filter(isLazy)
    }
  }

  private def isLazy(param: Term.Param): Boolean = param.mods.contains(q"@_root_.com.ted.`lazy`.Lazy")

  //  private def adjustVariableUsages(lazyVals: Map[String, (Term.Name, Defn.Val)], body: Term): Term = {
  //    // TODO 1. add the val definitions to `body`
  //    // 2. replace occurrences of the `lazyVal` keys with the newly declared lazy vals (extract from `lazyVals` values)
  //
  //    val updatedBody = replaceNonLazyOccurences(lazyVals.mapValues(_._1), body)
  //
  //    q"""
  //         ..${lazyVals.values.map(_._2).toList}
  //
  //         $updatedBody
  //        """
  //  }
  //
  //  private def replaceNonLazyOccurences(lazyVals: Map[String, Term.Name], body: Term): Term = body match {
  //    case apply@Term.Apply(Term.Name(name), _) if lazyVals.isDefinedAt(name) => apply.copy(fun = lazyVals(name))
  //    case other => ???; body.tree.syntax.parse[Term].get
  //  }
}

// todo check if only the function arguments are annotated and also check if the enclosing function is annotated with `@WithLazy`
class Lazy extends StaticAnnotation
