package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiEffect

/**
 * Side effects for Currency Exchange screen
 */
sealed class CurrencyExchangeEffect : UiEffect {
    data class ShowError(val message: String) : CurrencyExchangeEffect()
}

