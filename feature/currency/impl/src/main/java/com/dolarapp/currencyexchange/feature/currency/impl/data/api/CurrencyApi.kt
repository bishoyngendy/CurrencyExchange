package com.dolarapp.currencyexchange.feature.currency.impl.data.api

import com.dolarapp.currencyexchange.feature.currency.impl.data.model.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for currency exchange
 * Currently defined for future real API integration
 */
interface CurrencyApi {
    /**
     * Get exchange rates for a base currency
     * Example: GET /latest/USD
     */
    @GET("latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") baseCurrency: String
    ): ExchangeRatesResponse
}

