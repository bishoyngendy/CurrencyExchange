package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiState
import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker

/**
 * UI State for Currency Exchange screen
 */
data class CurrencyExchangeState(
    val isLoading: Boolean = false,
    val currencies: List<String> = emptyList(),
    val selectedCurrencies: List<String> = emptyList(),
    val tickers: List<CurrencyTicker> = emptyList(),
    val error: String? = null
) : UiState

