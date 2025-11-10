plugins {
    alias(libs.plugins.ecarepro.android.feature)
    alias(libs.plugins.ecarepro.android.library.compose)
}

android {
    namespace = "com.app.ecarepro.feature.timetable"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}