package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiIntent

/**
 * User intents for Currency Exchange screen
 */
sealed class CurrencyExchangeIntent : UiIntent {
    data object LoadCurrencies : CurrencyExchangeIntent()
    // Calculator intents
    data class SetFromCurrency(val currency: String) : CurrencyExchangeIntent()
    data class SetToCurrency(val currency: String) : CurrencyExchangeIntent()
    data class SetFromAmount(val amount: String) : CurrencyExchangeIntent()
    data class SetToAmount(val amount: String) : CurrencyExchangeIntent()
    data object SwapCurrencies : CurrencyExchangeIntent()
}

