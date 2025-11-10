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
    }
}

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

    // --- CORRECT SUPABASE DEPENDENCIES USING THE VERSION CATALOG ---
    implementation(platform(libs.supabase.bom)) // Use the alias from libs.versions.toml
    implementation(libs.supabase.postgrest)     // Use the alias
    implementation(libs.supabase.auth)         // Use the alias

    // Ktor Client is required by Supabase
    implementation("io.ktor:ktor-client-android:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Coroutine support
    implementation(libs.kotlinx.coroutines.android)
}
