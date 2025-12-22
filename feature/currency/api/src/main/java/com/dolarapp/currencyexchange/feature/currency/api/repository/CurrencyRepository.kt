package com.dolarapp.currencyexchange.feature.currency.api.repository

import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for currency exchange data
 * Defines the public API for accessing currency information
 */
interface CurrencyRepository {
    /**
     * Get available currencies from the API
     * Falls back to hardcoded list if API fails
     * @return Flow of available currency codes
     */
    fun getAvailableCurrencies(): Flow<List<String>>

    /**
     * Get available currencies (fake/hardcoded)
     * Always returns a hardcoded list for testing
     * @return Flow of hardcoded currency codes
     */
    fun getAvailableCurrenciesFake(): Flow<List<String>>

    /**
     * Get tickers for specified currencies
     * @param currencies List of currency codes to fetch
     * @return Flow of currency tickers
     */
    fun getTickers(currencies: List<String>): Flow<List<CurrencyTicker>>
}

