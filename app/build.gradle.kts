@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once TIJA-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.firebaseCrashlytics)
    id("com.google.gms.google-services")
    alias(libs.plugins.safeArgs)
    kotlin("kapt")
    alias(libs.plugins.kotlinParcelize)
    id("com.google.devtools.ksp")
    id("com.google.firebase.firebase-perf")
    alias(libs.plugins.compose)
    alias(libs.plugins.ecarepro.android.application.compose)

}

kapt {
    correctErrorTypes = true
}

android {
    signingConfigs {
        create("Franciscan e-Care") {
            storeFile = file("eCarePro.jks")
            storePassword = "eCarePro"
            keyAlias = "ecareProAndroid"
            keyPassword = "eCarePro"
        }
        create("MYSFHS") {
            storeFile = file("strawberry_key")
            storePassword = "strawberryapp"
            keyAlias = "strawberryapp"
            keyPassword = "strawberryapp"
        }
        create("MYSFPS Play") {
            storeFile = file("strawberry_play_key")
            storePassword = "strawberryplay"
            keyAlias = "strawberryplay"
            keyPassword = "strawberryplay"
        }
    }
    namespace = "com.app.ecarepro"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.franciscan.ecare_pro"
        minSdk = 23
        targetSdk = 36
        versionCode = 373
        versionName = "3.6.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //add this in the build.gradle.kts(app) file
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] =
                    "$projectDir/schemas"
            }
            ksp {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
        resConfigs("en", "hi", "gu")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("uat") {
            initWith(buildTypes.getByName("debug"))
            isMinifyEnabled = false
            isDebuggable = true
            matchingFallbacks += listOf("debug")
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-UAT"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
        compose = true
    }
    flavorDimensions += listOf("apps")

    productFlavors {
        create("Franciscane-Care") {
            dimension = "apps"
            resValue("string", "app_name", "Franciscan e-Care")
            signingConfig = signingConfigs.getByName("Franciscan e-Care")
        }
        create("MYSFHS") {
            dimension = "apps"
            resValue("string", "app_name", "MYSFHS")
            applicationId = "com.franciscan.strawberry"
            versionCode = 40
            versionName = "2.0.15"
            signingConfig = signingConfigs.getByName("MYSFHS")
        }
        create("MYSFPSPlay") {
            dimension = "apps"
            resValue("string", "app_name", "MYSFPS")
            applicationId = "com.franciscan.strawberry_play"
            versionCode = 5
            versionName = "1.0.4"
            signingConfig = signingConfigs.getByName("MYSFPS Play")
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

}

dependencies {

    implementation(projects.feature.splash)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.schoolcode)
    implementation(projects.feature.login)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.timetable)
    implementation(projects.core.data)

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
    implementation(libs.com.google.firebase.firebase.crashlytics)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    kapt(libs.hilt.android.compiler)

    /*    *//* Database *//*
    implementation(libs.androidx.room.runtime)
   // kapt(libs.androidx.room.compiler)
    annotationProcessor("androidx.room:room-compiler:2.5.1")
    implementation(libs.androidx.room.ktx)*/
    val roomVersion = "2.5.2"
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
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
    implementation(libs.firebase.messaging)
    implementation("com.google.firebase:firebase-perf")
    /* OTP Pin View */
    implementation(libs.otpview)

    /* Epoxy Recycler View which   is  multiple  view   handle in  one  view  */
    implementation(libs.epoxy)
    kapt(libs.epoxy.processor)
    implementation(libs.epoxy.databinding)

    /* RecyclerView Item Decorations */
    implementation(libs.decorator)

    /*AAChartCore-Kotlin is a Kotlin library typically used in User Interface*/
    implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.2.0")

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
    implementation("com.github.Mindinventory:Lassi:1.4.2")
    implementation("com.github.AsynctaskCoffee:VoiceRecorder:beta-0.5")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("com.github.fornewid:neumorphism:0.3.2")
    implementation("androidx.browser:browser:1.2.0")
    implementation("com.android.support:print:28.0.0")
    implementation("uk.co.samuelwall:material-tap-target-prompt:3.3.2")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    implementation  ( "com.google.firebase:firebase-messaging-ktx")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
    implementation("com.kizitonwose.calendar:view:2.5.4")

    implementation ("com.github.bumptech.glide:glide:4.4.0")
    kapt ("com.github.bumptech.glide:compiler:4.4.0")

    // This dependency is downloaded from the Google’s Maven repository.
    // So, make sure you also include that repository in your project's build.gradle file.
    implementation("com.google.android.play:app-update:2.1.0")

    // For Kotlin users also import the Kotlin extensions library for Play In-App Update:
    implementation("com.google.android.play:app-update-ktx:2.1.0")


    // Re-vamp
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
 //   implementation(libs.androidx.compose.material3.adaptive.navigation)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)

}