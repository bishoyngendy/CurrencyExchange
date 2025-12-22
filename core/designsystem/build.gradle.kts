plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.dolarapp.currencyexchange.core.designsystem"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
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
    // AndroidX Core (for WindowCompat and Activity APIs)
    implementation(libs.androidx.core.ktx)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    
    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    
    // Material 3
    implementation(libs.androidx.compose.material3)
    
    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
}

