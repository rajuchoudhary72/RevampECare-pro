plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
}

android {
    namespace = "com.app.ecarepro.core.download"
}

dependencies {
    implementation(projects.core.domain)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
