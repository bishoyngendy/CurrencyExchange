plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dolarapp.currencyexchange.feature.currency.data"
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
}

dependencies {
    // Feature API
    implementation(project(":feature:currency:api"))
    
    // Core modules
    implementation(project(":core:network"))
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

