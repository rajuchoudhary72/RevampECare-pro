plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.app.ecarepro.core.network"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logging.interceptor)
    api(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
}

