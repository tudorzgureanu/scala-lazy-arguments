package com.tudorzgureanu.lazyargs

import org.scalatest.{Matchers, WordSpecLike}

class WithLazySpec extends WordSpecLike with Matchers {

  "WithLazy" when {
    "annotates a simple method" should {
      @WithLazy
      def lazyFoo(cond: Boolean)(@Lazy bar: String) =
        if (cond) bar + bar else ""

      "evaluate the @Lazy argument only once" in {
        var nastyVar = 0

        val result = lazyFoo(cond = true) {
          nastyVar += 1
          "bar "
        }

        result shouldBe "bar bar "
        nastyVar shouldBe 1
      }

      "not evaluate the @Lazy argument at all if not required" in {
        var nastyVar = 0

        val result = lazyFoo(cond = false) {
          nastyVar += 1
          "bar "
        }

        result shouldBe ""
        nastyVar shouldBe 0
      }

      "evaluate the non-annotated with @Lazy `bar` argument even though it is not used" in {
        @WithLazy
        def foo(bar: String) = ""
        var nastyVar = 0

        val result = foo {
          nastyVar += 1
          "bar "
        }

        result shouldBe ""
        nastyVar shouldBe 1
      }
    }

    "annotates a recursive method" should {
      @WithLazy
      def times[T](t: Int)(@Lazy elem: T): Seq[T] = t match {
        case t if t > 0 => elem +: times(t - 1)(elem)
        case _ => Seq.empty
      }

      "evaluate the @Lazy argument only once" in {
        var nastyVar = 0

        val result = times(3) {
          nastyVar += 1
          nastyVar
        }

        result shouldBe Seq(1, 1, 1)
        nastyVar shouldBe 1
      }

      "not evaluate the @Lazy argument at all if not required " in {
        var nastyVar = 0

        val result = times(0) {
          nastyVar += 1
          nastyVar
        }

        result shouldBe Seq.empty
        nastyVar shouldBe 0
      }
    }
  }
}
