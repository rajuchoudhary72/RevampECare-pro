plugins {
    alias(libs.plugins.ecarepro.android.feature)
    alias(libs.plugins.ecarepro.android.library.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.app.ecarepro.feature.navigation"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(projects.core.domain)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}