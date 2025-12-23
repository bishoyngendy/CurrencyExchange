package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigDecimal

/**
 * Currency Exchange calculator screen
 * Wrapper that connects ViewModel to pure composable
 */
@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load currencies on initial composition
    LaunchedEffect(Unit) {
        viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadCurrencies)
    }

    // Compute exchange rate text
    val exchangeRateText = viewModel.computeDisplayExchangeRate()?.let { (rate, otherCurrency) ->
        rate?.let { "1 USDc = ${formatExchangeRate(it)} $otherCurrency" } ?: ""
    } ?: ""

    CurrencyExchangeContent(
        state = state,
        exchangeRateText = exchangeRateText,
        onLoadCurrencies = {
            viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadCurrencies)
        },
        onSetFromCurrency = { currency ->
            viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromCurrency(currency))
        },
        onSetToCurrency = { currency ->
            viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToCurrency(currency))
        },
        onSetFromAmount = { amount ->
            viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromAmount(amount))
        },
        onSetToAmount = { amount ->
            viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToAmount(amount))
        },
        onSwapCurrencies = {
            viewModel.processIntentOnMain(CurrencyExchangeIntent.SwapCurrencies)
        }
    )
}

/**
 * Formats exchange rate with commas every 3 digits before decimal point
 */
private fun formatExchangeRate(rate: BigDecimal): String {
    val plainString = rate.toPlainString()
    if (plainString.contains(".")) {
        val parts = plainString.split(".")
        val integerPart = parts[0]
        val decimalPart = parts[1]
        return "${addCommasToInteger(integerPart)}.$decimalPart"
    } else {
        return addCommasToInteger(plainString)
    }
}

/**
 * Adds commas to an integer string every 3 digits from right to left
 */
private fun addCommasToInteger(integerString: String): String {
    if (integerString.isEmpty()) return ""
    
    val isNegative = integerString.startsWith("-")
    val digits = if (isNegative) integerString.substring(1) else integerString
    
    val result = StringBuilder()
    var count = 0
    
    for (i in digits.length - 1 downTo 0) {
        if (count > 0 && count % 3 == 0) {
            result.insert(0, ",")
        }
        result.insert(0, digits[i])
        count++
    }
    
    return if (isNegative) "-$result" else result.toString()
}
