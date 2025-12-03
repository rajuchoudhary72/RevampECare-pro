
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
