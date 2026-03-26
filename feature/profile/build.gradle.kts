plugins {
    alias(libs.plugins.ecarepro.android.feature)
    alias(libs.plugins.ecarepro.android.library.compose)
    alias(libs.plugins.ecarepro.hilt)
}

android {
    namespace = "com.app.ecarepro.feature.profile"
}

dependencies {
    implementation(projects.core.domain)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
