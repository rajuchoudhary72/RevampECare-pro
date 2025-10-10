plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
}

android {
    namespace = "com.app.ecarepro.core.domain"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
