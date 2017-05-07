package com.ted

import scala.annotation.StaticAnnotation
import scala.meta.{Term, _}
import scala.collection.immutable.Seq
import scala.meta.contrib._

class WithLazy extends StaticAnnotation {

  import WithLazy._

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        val lazyAnnotatedParams = collectLazyArgs(defn.paramss)

        val localLazyVals: Seq[(String, (Term.Name, Defn.Val))] = lazyAnnotatedParams.flatMap(_.map {
          case param@Term.Param(_, name, Some(Type.Arg.ByName(typeArg)), _) =>
            // transform by-value args to by-name

            val lazyTerm = Pat.Var.Term(Term.Name(name.syntax + "Lazy"))
            name.syntax -> (lazyTerm.name -> q"lazy val $lazyTerm: $typeArg = ${Term.Name(param.name.value)}")
        })

        // todo adjust all the expressions in the body to use the lazy versions of params
        defn.copy(body = adjustVariableUsages(localLazyVals.toMap, defn.body))
    }
  }
}

object WithLazy {
  private def collectLazyArgs(paramss: Seq[Seq[Term.Param]]): Seq[Seq[Term.Param]] = {
    paramss.map { paramList =>
      paramList.filter {
        _.mods.exists {
          case q"@Lazy" => true
          case _ => false
        }
      }
    }
  }

  private def adjustVariableUsages(lazyVals: Map[String, (Term.Name, Defn.Val)], body: Term): Term = {
    // TODO 1. add the val definitions to `body`
    // 2. replace occurrences of the `lazyVal` keys with the newly declared lazy vals (extract from `lazyVals` values)

    val updatedBody = replaceNonLazyOccurences(lazyVals.mapValues(_._1), body)

    q"""
         ..${lazyVals.values.map(_._2).toList}

         $updatedBody
        """
  }

  private def replaceNonLazyOccurences(lazyVals: Map[String, Term.Name], body: Term): Term = body match {
    case _ => ???
  }
}

// todo check if only the function arguments are annotated and also check if the enclosing function is annotated with `@WithLazy`
class Lazy extends StaticAnnotation
