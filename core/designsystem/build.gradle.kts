plugins {
    alias(libs.plugins.ecarepro.android.androidLibrary)
    alias(libs.plugins.ecarepro.android.library.compose)
}

android {
    namespace = "com.app.ecarepro.core.designsystem"
    testOptions.unitTests.isIncludeAndroidResources = true
}
dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
 //   api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)


    implementation(libs.coil.compose)
    implementation(libs.coil.gif)    // For GIF support
    implementation(libs.lottie.compose)



}