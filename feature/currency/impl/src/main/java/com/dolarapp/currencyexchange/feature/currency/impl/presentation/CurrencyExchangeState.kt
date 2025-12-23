package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import com.dolarapp.currencyexchange.core.mvi.UiState
import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import java.math.BigDecimal

/**
 * UI State for Currency Exchange screen
 */
data class CurrencyExchangeState(
    val screenStatus: ScreenStatus = ScreenStatus.Idle,
    
    // Available currencies
    val currencies: List<String> = emptyList(),
    
    // Market data indexed by currency code (MXN, ARS, etc.)
    val tickers: Map<String, CurrencyTicker> = emptyMap(),
    
    // Calculator
    val calculator: CalculatorState = CalculatorState()
) : UiState

/**
 * Screen status for loading and error states
 */
sealed interface ScreenStatus {
    data object Idle : ScreenStatus
    data object Loading : ScreenStatus
    data class Error(val message: String) : ScreenStatus
}

/**
 * Calculator state
 */
data class CalculatorState(
    val baseCurrency: String = "USDc",
    val quoteCurrency: String = "MXN",
    val baseAmount: String = "",
    val quoteAmount: String = "",
    val isConvertingFromUSDc: Boolean = true, // true = converting from USDc to other currency (use ask), false = converting to USDc (use bid)
    val rate: BigDecimal? = null // Current rate (ask or bid depending on direction)
)

