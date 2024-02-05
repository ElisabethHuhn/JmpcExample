

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.huhn.jmpcexample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.huhn.jmpcexample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //permissions
    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation ("com.google.accompanist:accompanist-permissions:0.33.2-alpha")

    //location from google play services
    implementation ("com.google.android.gms:play-services-location:21.1.0")
//    implementation ("com.example:multiplepermissionsstate:1.0.0")

    //compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")

    // Lifecycle components
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.0")

    //navigation
    implementation ("androidx.navigation:navigation-compose:2.7.6")

    //kotlin coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    //coroutine lifecycle scopes
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //Koin for dependency
//    implementation ("io.insert-koin:koin-android:3.3.1")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")


    //Room Library
//    implementation ("androidx.room:room-runtime:2.5.1")
//    // Kotlin Extensions and Coroutines support for Room
//    implementation ("androidx.room:room-ktx:2.5.1")
//    kapt ("androidx.room:room-compiler:2.5.1")
//    kapt ("android.arch.persistence.room:compiler:1.1.1")
//
//    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")



    //retrofit
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //coil for image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    //Picasso
//    implementation ("com.squareup.picasso:picasso:2.71828")
//    implementation ("com.jakewharton.picasso:picasso2-okhttp3-downloader:1.1.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("com.google.truth:truth:1.0.1")
    androidTestImplementation ("android.arch.core:core-testing:1.1.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    // Test rules and transitive dependencies:
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}