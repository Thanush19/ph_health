package com.example.detekt

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetektPlayground {

    // 1. Magic numbers
    fun calculatePrice(price: Double): Double {
        return price * 0.18 + 99.0
    }

    // 2. Too many parameters
    fun createUser(
        name: String,
        email: String,
        age: Int,
        city: String,
        country: String,
        phone: String,
        address: String
    ) {
        println(name)
    }

    // 3. Long method
    fun veryLongMethod() {
        val a = 1
        val b = 2
        val c = 3
        val d = 4
        val e = 5
        val f = 6
        val g = 7
        val h = 8
        val i = 9
        val j = 10
        val k = 11
        val l = 12
        val m = 13
        val n = 14
        val o = 15
        val p = 16
        val q = 17
        val r = 18
        val s = 19
        val t = 20

        println(a)
        println(b)
        println(c)
        println(d)
        println(e)
        println(f)
        println(g)
        println(h)
        println(i)
        println(j)
        println(k)
        println(l)
        println(m)
        println(n)
        println(o)
        println(p)
        println(q)
        println(r)
        println(s)
        println(t)

        println("line 1")
        println("line 2")
        println("line 3")
        println("line 4")
        println("line 5")
        println("line 6")
        println("line 7")
        println("line 8")
        println("line 9")
        println("line 10")
        println("line 11")
        println("line 12")
        println("line 13")
        println("line 14")
        println("line 15")
        println("line 16")
        println("line 17")
        println("line 18")
        println("line 19")
        println("line 20")
        println("line 21")
        println("line 22")
        println("line 23")
        println("line 24")
        println("line 25")
        println("line 26")
        println("line 27")
        println("line 28")
        println("line 29")
        println("line 30")
    }

    // 4. High cyclomatic complexity
    fun processUser(
        age: Int,
        isPremium: Boolean,
        isVerified: Boolean,
        hasSubscription: Boolean,
        country: String
    ): String {

        if (age > 18) {

            if (isPremium) {

                if (isVerified) {
                    println("premium verified")
                } else {
                    println("premium not verified")
                }

                if (hasSubscription) {
                    println("has subscription")
                } else {
                    println("no subscription")
                }

            } else {

                if (isVerified) {
                    println("free verified")
                } else {
                    println("free not verified")
                }
            }

        } else {
            println("minor")
        }

        if (country == "India") {
            println("India")
        } else if (country == "USA") {
            println("USA")
        } else if (country == "UK") {
            println("UK")
        } else {
            println("Other")
        }

        for (i in 0..10) {
            if (i % 2 == 0) {
                println(i)
            }
        }

        return if (isPremium && isVerified) {
            "PREMIUM"
        } else if (isVerified || hasSubscription) {
            "USER"
        } else {
            "GUEST"
        }
    }

    // 5. Nested blocks
    fun deeplyNested(value: Int) {

        if (value > 0) {
            if (value > 10) {
                if (value > 20) {
                    if (value > 30) {
                        if (value > 40) {
                            println("very large")
                        }
                    }
                }
            }
        }
    }

    // 6. Bad exception handling
    fun loadData() {
        try {
            println("loading")
        } catch (e: Exception) {
            println("Something went wrong")
        }
    }

    // 7. Forbidden API / GlobalScope
    fun launchSomething() {

        GlobalScope.launch {
            println("running")
        }
    }

    @Composable
    fun composeExample() {
        Text("Hello")
    }

    // 9. println
    fun debug() {
        println("debug message")
    }
}