package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiIntent

/**
 * User intents for Currency Exchange screen
 */
sealed class CurrencyExchangeIntent : UiIntent {
    data object LoadRates : CurrencyExchangeIntent()
    data object ConvertCurrency : CurrencyExchangeIntent()
    data object SwapCurrencies : CurrencyExchangeIntent()
    data class SelectFromCurrency(val currency: String) : CurrencyExchangeIntent()
    data class SelectToCurrency(val currency: String) : CurrencyExchangeIntent()
    data class UpdateAmount(val amount: String) : CurrencyExchangeIntent()
}

