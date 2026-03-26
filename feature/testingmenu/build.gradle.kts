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
    implementation(projects.feature.studentprofile)
    implementation(projects.feature.staffprofile)
    implementation(projects.feature.taskmanger)
    implementation(projects.feature.leave)
    implementation(projects.feature.discipline)
    implementation(projects.feature.transportAtt)
    implementation(projects.feature.message)
    implementation(projects.feature.announcement)
    implementation(projects.feature.gallery)
    implementation(projects.feature.updateRecord)
    implementation(projects.feature.calendar)
    implementation(projects.feature.survey)
    implementation(projects.feature.library)
    implementation(projects.feature.ebook)
    implementation(projects.feature.fee)
    implementation(projects.feature.feed)

    implementation(projects.feature.report)

    implementation(projects.feature.smsdailyconsumption)
    implementation(projects.feature.conversationreport)
    implementation(projects.feature.globalsearch)
    implementation(projects.feature.setting)
    implementation(projects.feature.knowyourteacher)
    implementation(projects.feature.classteacher)
    implementation(projects.feature.profile)
    implementation(projects.feature.notice)
    implementation(projects.feature.questionpaper)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}