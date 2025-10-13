plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.hilt)
    alias(libs.plugins.ecarepro.android.room)
}

android {
    namespace = "com.app.ecarepro.core.database"
}

dependencies {
    api(projects.core.domain)
}