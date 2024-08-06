@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.safeArgs)
    kotlin("kapt")
    alias(libs.plugins.kotlinParcelize)
}

kapt {
    correctErrorTypes = true
}

android {
    signingConfigs {
        create("beta") {
            storeFile = file("eCareBeta.jks")
            storePassword = "eCarePro"
            keyAlias = "ecareProAndroidBeta"
            keyPassword = "eCarePro"
        }
    }
    namespace = "com.app.ecarepro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.franciscan.ecare_pro"
        minSdk = 23
        targetSdk = 34
        versionCode = 13
        versionName = "1.1.11"
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
            resValue("string", "app_name", "e-Care-Dev")
        }
        create("prod") {
            dimension = "build"
            resValue("string", "app_name", "e-Care")
        }
        create("beta") {
            dimension = "build"
            applicationIdSuffix = ".beta"
            resValue("string", "app_name", "e-Care")
            signingConfig = signingConfigs.getByName("beta")
        }
    }

}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    /* Dependency Injection -> Hilt */
    implementation(libs.hilt.android)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
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

    /* OTP Pin View */
    implementation(libs.otpview)

    /* Epoxy Recycler View which   is  multiple  view   handle in  one  view  */
    implementation(libs.epoxy)
    kapt(libs.epoxy.processor)
    implementation(libs.epoxy.databinding)

    /* RecyclerView Item Decorations */
    implementation(libs.decorator)

    /*AAChartCore-Kotlin is a Kotlin library typically used in User Interface*/
    implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.2.1")

    /* Page indicator */
    implementation(libs.scrollingpagerindicator)
    implementation(libs.dotsindicator)

    /*An image loading library for Android backed by Kotlin Coroutines*/
    //noinspection UseTomlInstead
    implementation("io.coil-kt:coil:2.6.0")
    //noinspection UseTomlInstead
    implementation("io.coil-kt:coil-svg:2.6.0")

    implementation(libs.picasso)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //implementation("com.caverock:androidsvg:1.4")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.intuit.sdp:sdp-android:1.0.5")
    implementation("de.hdodenhof:circleimageview:2.2.0")
    implementation("com.github.Mindinventory:Lassi:1.3.0")
    implementation("com.github.AsynctaskCoffee:VoiceRecorder:beta-0.5")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("com.github.fornewid:neumorphism:0.3.2")


}