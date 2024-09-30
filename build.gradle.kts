buildscript {
    dependencies {
         classpath("com.android.tools.build:gradle:8.1.0")
         classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
    alias(libs.plugins.kotlinParcelize) apply false
}
true // Needed to make the Suppress annotation work for the plugins block