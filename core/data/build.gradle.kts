plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
}

android {
    namespace = "com.app.ecarepro.core.data"
}

dependencies {
    api(projects.core.network)
    api(projects.core.domain)
}
