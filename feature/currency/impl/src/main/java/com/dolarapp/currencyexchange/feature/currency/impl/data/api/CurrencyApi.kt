package com.dolarapp.currencyexchange.feature.currency.impl.data.api

import com.dolarapp.currencyexchange.feature.currency.impl.data.dto.CurrencyTickerDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for currency exchange
 * Base URL: https://api.dolarapp.dev/v1/
 */
interface CurrencyApi {
    /**
     * Get tickers for specified currencies
     * GET tickers?currencies=MXN,ARS
     * 
     * @param currencies Comma-separated list of currency codes
     * @return List of currency tickers
     */
    @GET("tickers")
    suspend fun getTickers(
        @Query("currencies") currencies: String
    ): List<CurrencyTickerDto>

    /**
     * Get available currencies
     * GET tickers-currencies
     * 
     * Note: This endpoint may not exist yet (404)
     * @return List of currency codes
     */
    @GET("tickers-currencies")
    suspend fun getAvailableCurrencies(): List<String>
}

