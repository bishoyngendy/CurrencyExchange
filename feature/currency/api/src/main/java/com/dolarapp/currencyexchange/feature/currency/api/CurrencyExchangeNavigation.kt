package com.dolarapp.currencyexchange.feature.currency.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Navigation route for Currency Exchange screen
 */
const val CURRENCY_EXCHANGE_ROUTE = "currency_exchange"

/**
 * Navigation contract for Currency Exchange feature
 * Provides the public API for navigating to the currency exchange screen
 * 
 * @param route The navigation route
 * @param content The composable content for the currency exchange screen
 *                This will be provided by the impl module
 */
fun NavGraphBuilder.currencyExchangeScreen(
    route: String = CURRENCY_EXCHANGE_ROUTE,
    content: @Composable () -> Unit
) {
    composable(route = route) {
        content()
    }
}

