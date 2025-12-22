package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiState

/**
 * UI State for Currency Exchange screen
 */
data class CurrencyExchangeState(
    val isLoading: Boolean = false,
    val amount: String = "",
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val convertedAmount: String = "",
    val exchangeRates: Map<String, Double> = emptyMap(),
    val availableCurrencies: List<String> = emptyList(),
    val error: String? = null
) : UiState

