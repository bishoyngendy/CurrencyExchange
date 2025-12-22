package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiIntent

/**
 * User intents for Currency Exchange screen
 */
sealed class CurrencyExchangeIntent : UiIntent {
    data class LoadCurrencies(val useFake: Boolean) : CurrencyExchangeIntent()
    data class SelectCurrency(val currency: String) : CurrencyExchangeIntent()
    data object LoadTickers : CurrencyExchangeIntent()
}

