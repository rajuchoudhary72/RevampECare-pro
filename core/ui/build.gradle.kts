plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.android.library.compose)
}

android {
    namespace = "com.app.ecarepro.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    implementation(libs.androidx.lifecycle.viewModelKtx)
}
