
plugins {
    id("com.saivishnu.lox.java-application-conventions")
}

// dependencies {}

application {
    // Define the main class for the application.
    mainClass.set("com.saivishnu.lox.Lox")
}


// tasks.register("hello") {
//     doLast {
//         println("Hello ")
//     }
// }

// tasks.register("world") {
//     println("World!")
//     dependsOn("hello")
// }