package com.ted.lazyargs

import org.scalatest.{Matchers, WordSpecLike}

class WithLazySpec extends WordSpecLike with Matchers {

  "WithLazy" when {
    "annotates a simple method" should {
      @WithLazy
      def lazyFoo(cond: Boolean)(@Lazy bar: String) = if (cond) bar + bar else ""

      "evaluate the @Lazy block only once on `cond` being true" in {
        var nastyVar = 0

        val result = lazyFoo(cond = true) {
          nastyVar += 1
          "bar "
        }

        result shouldBe "bar bar "
        nastyVar shouldBe 1
      }

      "not evaluate the @Lazy block at all on `cond` being false" in {
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

        val result = foo{
          nastyVar += 1
          "bar "
        }

        result shouldBe ""
        nastyVar shouldBe 1
      }
    }
  }
}
