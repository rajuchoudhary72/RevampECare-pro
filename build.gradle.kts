
plugins {
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
   // id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.room) apply false
}

// Custom task to deep clean build files
tasks.register<Delete>("cleanAll") {
    group = "build"
    description = "Deep clean: removes build directories and clears Gradle caches to fix transform issues"

    // Delete build directories
    delete(layout.buildDirectory)

    subprojects.forEach { subproject ->
        delete(subproject.layout.buildDirectory)
    }

    doLast {
        println("✅ Build directories cleaned")
        println("ℹ️  To clear Gradle cache, run: rm -rf ~/.gradle/caches/8.14.3/transforms")
        println("ℹ️  Or manually: ./gradlew clean && rm -rf ~/.gradle/caches/*/transforms")
    }
}

// Task that depends on clean and provides instructions
tasks.register("cleanWithInstructions") {
    group = "build"
    description = "Clean build and show instructions to clear Gradle cache if needed"

    dependsOn("clean")

    doLast {
        println("\n" + "=".repeat(70))
        println("✅ Project cleaned successfully!")
        println("=".repeat(70))
        println("\nIf you encounter transform cache errors, run one of these:")
        println("  1. ./gradlew cleanAll")
        println("  2. rm -rf ~/.gradle/caches/8.14.3/transforms")
        println("\nThen rebuild your project.")
        println("=".repeat(70) + "\n")
    }
}
