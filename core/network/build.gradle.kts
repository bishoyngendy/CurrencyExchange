plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dolarapp.currencyexchange.core.network"
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
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Network - exposed via api() so types are visible to downstream modules for KSP
    api(libs.retrofit)
    api(libs.retrofit.moshi)
    api(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor) // Internal detail, not exposed
    
    // Moshi - exposed via api() so types are visible to downstream modules for KSP
    api(libs.moshi)
    api(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen) // Code generation, not type exposure
    
    // Core config (for base URLs)
    implementation(project(":core:config"))
}

