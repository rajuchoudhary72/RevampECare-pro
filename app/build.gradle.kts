@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleServices)
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.app.ecarepro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.franciscan.ecare_pro"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    flavorDimensions += listOf("build")
    productFlavors {
        create("dev") {
            dimension = "build"
        }
        create("prod") {
            dimension = "build"
        }
    }

}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation (libs.androidx.recyclerview)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation (libs.androidx.activity.ktx)
    /* Dependency Injection -> Hilt */
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    /* Database */
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    /* Preferences */
    implementation(libs.androidx.datastore.preferences)

    /* Network -> Retrofit, OkHttp */
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    /* Firebase */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.cloud.messaging)

    /* Pin View */
    implementation(libs.otpview)

    /* Epoxy Recycler View */
    implementation(libs.epoxy)
    kapt(libs.epoxy.processor)
    implementation(libs.epoxy.databinding)

    /* RecyclerView Item Decorations */
    implementation(libs.decorator)

    implementation ("com.github.AAChartModel:AAChartCore-Kotlin:7.2.1")

    /* Page indicator */
    implementation (libs.scrollingpagerindicator)
    implementation(libs.dotsindicator)

    implementation("io.coil-kt:coil:2.5.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

}