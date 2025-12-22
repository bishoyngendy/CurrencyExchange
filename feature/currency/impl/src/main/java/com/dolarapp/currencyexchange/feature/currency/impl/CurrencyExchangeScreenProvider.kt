package com.dolarapp.currencyexchange.feature.currency.impl

import androidx.compose.runtime.Composable
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CurrencyExchangeScreen

/**
 * Provides the Currency Exchange screen composable
 * This allows the app module to use the screen from the impl module
 * without directly depending on impl module internals
 */
@Composable
fun CurrencyExchangeScreenProvider() {
    CurrencyExchangeScreen()
}

