plugins {
    alias(libs.plugins.ecarepro.android.feature)
    alias(libs.plugins.ecarepro.android.library.compose)
}

android {
    namespace = "com.app.ecarepro.feature.testingmenu"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(projects.feature.splash)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.schoolcode)
    implementation(projects.feature.login)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.timetable)
    implementation(projects.feature.syllabus)
    implementation(projects.feature.assignment)
    implementation(projects.feature.docviewer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}