package ru.romanow.kotlin.lecture

fun <A, B, C> ((A) -> B).andThen(action: (B) -> C): (A) -> C = { a: A -> action(this(a)) }

fun main() {
    val exp: (Int) -> Int = { x: Int -> x * x }
    val println: (Int) -> Unit = { i: Int -> println("Result: $i") }

    exp.andThen {
        println("Hello $it")
        return@andThen it
    }
        .andThen(println)
        .invoke(5)
}