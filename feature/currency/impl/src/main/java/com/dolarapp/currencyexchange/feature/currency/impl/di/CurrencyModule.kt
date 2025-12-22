package com.dolarapp.currencyexchange.feature.currency.impl.di

import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository
import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi
import com.dolarapp.currencyexchange.feature.currency.impl.data.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module for Currency Exchange feature dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository

    companion object {
        @Provides
        @Singleton
        fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
            return retrofit.create(CurrencyApi::class.java)
        }
    }
}

