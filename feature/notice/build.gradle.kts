plugins {
    alias(libs.plugins.ecarepro.android.feature)
    alias(libs.plugins.ecarepro.android.library.compose)
    alias(libs.plugins.ecarepro.hilt)
}

android {
    namespace = "com.app.ecarepro.feature.notice"
}

dependencies {
    implementation(projects.core.data)
}
