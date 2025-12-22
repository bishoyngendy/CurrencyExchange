package com.dolarapp.currencyexchange.core.network

import com.dolarapp.currencyexchange.core.config.AppConfig
import com.dolarapp.currencyexchange.core.config.DefaultAppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing AppConfig
 * In a real app, this would be build-variant specific
 */
@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig {
        // In production, this would check BuildConfig or use different implementations
        return DefaultAppConfig()
    }
}

