package com.dolarapp.currencyexchange.feature.currency.impl.di

import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi
import com.dolarapp.currencyexchange.feature.currency.impl.data.repository.CurrencyRepository
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
object CurrencyModule {

    @Provides
    @Singleton
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        currencyApi: CurrencyApi
    ): CurrencyRepository {
        return CurrencyRepository(currencyApi)
    }

}

