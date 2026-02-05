package org.example

import kotlinx.coroutines.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() = runBlocking {
    println("=== Coroutines Playground ===\n")
    
    // Example 1: Basic coroutine
    println("1. Basic coroutine:")
    launch {
        delay(1000L)
        println("   Hello from coroutine!")
    }
    println("   Main thread continues...")
    delay(2000L)
    
    // Example 2: Async/await
    println("\n2. Async/await:")
    val deferred1 = async { 
        delay(500L)
        "Result 1"
    }
    val deferred2 = async { 
        delay(500L)
        "Result 2"
    }
    println("   ${deferred1.await()} and ${deferred2.await()}")
    
    // Example 3: Coroutine scope
    println("\n3. Coroutine scope:")
    coroutineScope {
        launch {
            delay(300L)
            println("   Task 1 completed")
        }
        launch {
            delay(300L)
            println("   Task 2 completed")
        }
    }
    println("   All tasks completed")
    
    // Example 4: Dispatchers
    println("\n4. Different dispatchers:")
    launch(Dispatchers.Default) {
        println("   Running on Default dispatcher (thread: ${Thread.currentThread().name})")
    }
    launch(Dispatchers.IO) {
        println("   Running on IO dispatcher (thread: ${Thread.currentThread().name})")
    }
    delay(500L)
    
    println("\n=== Playground finished ===")
}
