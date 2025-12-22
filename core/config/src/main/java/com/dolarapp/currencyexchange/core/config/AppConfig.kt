package com.dolarapp.currencyexchange.core.config

/**
 * Application-wide configuration interface
 * Provides abstraction for build variant-specific configurations
 */
interface AppConfig {
    /**
     * Base URL for API endpoints
     */
    val baseUrl: String
    
    /**
     * API key for currency exchange service (if needed)
     */
    val apiKey: String?
    
    /**
     * Whether the app is in debug mode
     */
    val isDebug: Boolean
}

/**
 * Default implementation for development/debug builds
 */
class DefaultAppConfig : AppConfig {
    override val baseUrl: String
        get() = "https://api.dolarapp.dev/v1/"
    
    override val apiKey: String?
        get() = null
    
    override val isDebug: Boolean
        get() = true
}

/**
 * Production implementation
 */
class ProductionAppConfig : AppConfig {
    override val baseUrl: String
        get() = "https://api.dolarapp.dev/v1/"
    
    override val apiKey: String?
        get() = null
    
    override val isDebug: Boolean
        get() = false
}

