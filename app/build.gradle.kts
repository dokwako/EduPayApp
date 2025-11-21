plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // You correctly added the serialization plugin
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.edupayapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.edupayapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add the build config fields with your Supabase credentials
        buildConfigField("String", "SUPABASE_URL", "\"https://erkqutbpbyqtnzasmdhj.supabase.co\"")
        buildConfigField("String", "SUPABASE_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVya3F1dGJwYnlxdG56YXNtZGhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4MTU4MTIsImV4cCI6MjA3NzM5MTgxMn0.R4p4-mesQFz2pBkETDCsQixet0Ks1MMjkpC5dZ66RVQ\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

val ktor_version = "3.2.2"

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.core:core-splashscreen:1.0.1")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    //
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // --- NEW DEPENDENCIES FROM YOUR FRIEND ---

    //Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.2"))
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.github.jan-tennert.supabase:compose-auth")

    //Ktor Client (compatible with Supabase 3.2.2)
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-auth:${ktor_version}")


    // --- END OF NEW DEPENDENCIES ---
    // Coroutine support
    implementation(libs.kotlinx.coroutines.android)
    // +++ ADD THE NEW PLAN (Retrofit and Gson) +++
//    implementation("com.squareup.retrofit2:retrofit:2.11.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
//    implementation("com.google.code.gson:gson:2.11.0")
}