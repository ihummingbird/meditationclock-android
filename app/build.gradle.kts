plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ihummingbird.meditationclock"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ihummingbird.meditationclock"
        minSdk = 24
        targetSdk = 35
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
    }
}

dependencies {
    implementation(libs.androidx.ui.graphics.android)
    // These are the only 3 things you need for a WebView app, actually even they are not needed
    // implementation("androidx.appcompat:appcompat:1.6.1")
    // implementation("androidx.core:core-ktx:1.12.0")
    // implementation("com.google.android.material:material:1.11.0")
}
