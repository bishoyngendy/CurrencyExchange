package com.dolarapp.currencyexchange.feature.currency.impl.data.repository

import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi
import com.dolarapp.currencyexchange.feature.currency.impl.data.model.CurrencyRate
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Repository for currency exchange data
 * Currently uses mocked data, but structured to easily switch to real API
 */
class CurrencyRepository @Inject constructor(
    private val currencyApi: CurrencyApi
) {
    /**
     * Get exchange rates for a base currency
     * Currently returns mocked data
     */
    suspend fun getExchangeRates(baseCurrency: String): Map<String, Double> {
        // Simulate network delay
        delay(500)
        
        // Mocked exchange rates (USD as base)
        return when (baseCurrency.uppercase()) {
            "USD" -> mapOf(
                "EUR" to 0.92,
                "GBP" to 0.79,
                "JPY" to 150.25,
                "AUD" to 1.52,
                "CAD" to 1.35,
                "CHF" to 0.88,
                "CNY" to 7.24,
                "INR" to 83.12
            )
            "EUR" -> mapOf(
                "USD" to 1.09,
                "GBP" to 0.86,
                "JPY" to 163.27,
                "AUD" to 1.65,
                "CAD" to 1.47,
                "CHF" to 0.96,
                "CNY" to 7.87,
                "INR" to 90.39
            )
            else -> mapOf(
                "USD" to 1.0,
                "EUR" to 0.92,
                "GBP" to 0.79,
                "JPY" to 150.25,
                "AUD" to 1.52,
                "CAD" to 1.35,
                "CHF" to 0.88,
                "CNY" to 7.24,
                "INR" to 83.12
            )
        }
    }

    /**
     * Get list of available currencies
     */
    fun getAvailableCurrencies(): List<String> {
        return listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR")
    }

    /**
     * Convert amount from one currency to another
     */
    suspend fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Double {
        if (fromCurrency == toCurrency) return amount
        
        val rates = getExchangeRates(fromCurrency)
        val rate = rates[toCurrency] ?: 1.0
        
        return amount * rate
    }
}

