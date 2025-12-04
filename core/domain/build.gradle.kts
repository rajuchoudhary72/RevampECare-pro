plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.app.ecarepro.core.domain"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
