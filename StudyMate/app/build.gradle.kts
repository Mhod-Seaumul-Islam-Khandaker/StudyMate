plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REMOVED: id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" // Causes version conflict
}

android {
    namespace = "com.example.studymate"
    compileSdk = 34 // Changed from 36 to 34 (more stable)

    defaultConfig {
        applicationId = "com.example.studymate"
        minSdk = 24 // Changed from 26 (better compatibility)
        targetSdk = 34 // Changed from 36 to match compileSdk
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

    // ✅ FIX #1: Build features must be INSIDE android{}
    buildFeatures {
        viewBinding = true  // CRITICAL for ActivityAuthBinding
    }

    compileOptions {
        // ✅ FIX #2: Use Java 17 (required for AGP 8+)
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // ✅ FIX #3: Match Java version
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX Core KTX and UI components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Using version catalog
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // CardView (use version catalog if possible)
    implementation("androidx.cardview:cardview:1.0.0")

    // ✅ REMOVED DUPLICATE: implementation("com.google.android.material:material:1.11.0")

    // ... your other Android dependencies ...

    // ✅ CORRECT SETUP for Supabase v3.x
    // Use the BOM (Bill of Materials) to manage compatible versions
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))

    // Use the module names for v3.x[citation:8]
    implementation("io.github.jan-tennert.supabase:auth-kt")  // Changed from 'gotrue-kt'
    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    // Ktor 3.x is required for supabase-kt 3.0.0[citation:8]
    implementation("io.ktor:ktor-client-android:3.0.0")
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}