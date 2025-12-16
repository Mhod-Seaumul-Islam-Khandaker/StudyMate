plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // The Google Services plugin needs to be applied in the application module
    id("com.google.gms.google-services") version "4.4.4" // Ensure you use the latest version here
}

android {
    namespace = "com.example.studymate"
    // Use direct assignment for SDK versions in Kotlin DSL
    compileSdk = 36
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.studymate"
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

    // Compile options use assignment with JavaVersion enum in Kotlin DSL
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Kotlin options
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Import the Firebase BoM FIRST to manage all versions automatically
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

    // AndroidX Core KTX and UI components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Explicit dependencies with hardcoded versions (outside of BoM management)
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase dependencies (version managed by the BoM)
    // Use the main 'firebase-auth' module (KTX features are now here)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.material:material:1.11.0")

}
