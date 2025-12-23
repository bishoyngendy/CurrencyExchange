package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dolarapp.currencyexchange.core.designsystem.components.CurrencyBottomSheet
import com.dolarapp.currencyexchange.core.designsystem.components.CurrencyCard
import com.dolarapp.currencyexchange.core.designsystem.components.CurrencyListItem
import com.dolarapp.currencyexchange.core.designsystem.components.LoadingIndicator
import com.dolarapp.currencyexchange.core.designsystem.components.SwapCurrencyButton
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.components.ExchangeRateText
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.components.ExchangeTitle
import com.dolarapp.currencyexchange.core.designsystem.theme.CurrencyCardBackground
import com.dolarapp.currencyexchange.core.designsystem.theme.ScreenBackground
import com.dolarapp.currencyexchange.core.designsystem.theme.Spacing
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Currency Exchange calculator screen
 * Matches the design from Figma
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }
    var isSelectingFromCurrency by remember { mutableStateOf(true) }

    // Load currencies on initial composition
    LaunchedEffect(Unit) {
        viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadCurrencies)
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        containerColor = ScreenBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.md) // 16px horizontal padding
                    .padding(top = 44.dp) // 44px top margin
            ) {
                // Title
                ExchangeTitle(
                    text = "Exchange calculator",
                    modifier = Modifier.padding(bottom = Spacing.sm) // 8px gap
                )

                // Exchange rate display - always visible, always shows "1 USDc = x other currency"
                ExchangeRateText(
                    text = viewModel.computeDisplayExchangeRate()?.let { (rate, otherCurrency) ->
                        rate?.let { "1 USDc = ${formatExchangeRate(it)} $otherCurrency" } ?: ""
                    } ?: "",
                    modifier = Modifier.padding(bottom = Spacing.md)
                )

                // Currency inputs wrapper column with overlapping button
                Box(modifier = Modifier.fillMaxWidth()) {
                    var columnHeight by remember { mutableStateOf(0) }
                    val density = LocalDensity.current
                    
                    // ViewA: Column with 2 currency cards (defines the height)
                    Column(
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            columnHeight = coordinates.size.height
                        }
                    ) {
                        // Base currency card
                        CurrencyCard(
                            currencyCode = state.calculator.baseCurrency,
                            amount = state.calculator.baseAmount,
                            isEditable = true,
                            isCurrencySelectable = !state.calculator.isConvertingFromUSDc, // Selectable if baseCurrency is not USDc
                            onCurrencyClick = {
                                // Base currency is selectable only when it's not USDc
                                if (!state.calculator.isConvertingFromUSDc) {
                                    isSelectingFromCurrency = true
                                    showCurrencyBottomSheet = true
                                }
                            },
                            onAmountChange = { amount ->
                                // Remove $ sign and commas if present, keep only numbers and decimal point
                                val cleanAmount = amount.replace("$", "").replace(",", "")
                                viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromAmount(cleanAmount))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(Spacing.md)) // Spacing between cards

                        // Quote currency card
                        CurrencyCard(
                            currencyCode = state.calculator.quoteCurrency,
                            amount = state.calculator.quoteAmount,
                            isEditable = true,
                            isCurrencySelectable = state.calculator.isConvertingFromUSDc, // Selectable if quoteCurrency is not USDc
                            onCurrencyClick = {
                                // Quote currency is selectable only when it's not USDc
                                if (state.calculator.isConvertingFromUSDc) {
                                    isSelectingFromCurrency = false
                                    showCurrencyBottomSheet = true
                                }
                            },
                            onAmountChange = { amount ->
                                // Remove $ sign and commas if present, keep only numbers and decimal point
                                val cleanAmount = amount.replace("$", "").replace(",", "")
                                viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToAmount(cleanAmount))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // ViewB: Box over ViewA with the same height, containing swap button centered vertically
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(with(density) { columnHeight.toDp() }),
                        contentAlignment = Alignment.Center
                    ) {
                        SwapCurrencyButton(
                            onClick = {
                                viewModel.processIntentOnMain(CurrencyExchangeIntent.SwapCurrencies)
                            }
                        )
                    }
                }
            }

            // Loading indicator
            if (state.screenStatus is ScreenStatus.Loading) {
                LoadingIndicator()
            }
        }
    }

    // Currency Selection Bottom Sheet (only for non-USDc currency)
    CurrencyBottomSheet(
        isVisible = showCurrencyBottomSheet,
        onDismiss = { showCurrencyBottomSheet = false },
        title = "Choose currency"
    ) {
        CurrencyPickerContent(
            currencies = state.currencies.filter { it != "USDc" }, // Exclude USDc from selection
            selectedCurrency = if (isSelectingFromCurrency) state.calculator.baseCurrency else state.calculator.quoteCurrency,
            onCurrencySelected = { currency ->
                // Set the appropriate currency based on which card was clicked
                if (isSelectingFromCurrency) {
                    viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromCurrency(currency))
                } else {
                    viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToCurrency(currency))
                }
                showCurrencyBottomSheet = false
            }
        )
    }
}

@Composable
private fun CurrencyPickerContent(
    currencies: List<String>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md)
            .clip(RoundedCornerShape(16.dp))
            .background(CurrencyCardBackground)
            .verticalScroll(rememberScrollState())
    ) {
        currencies.forEach { currency ->
            CurrencyListItem(
                currencyCode = currency,
                isSelected = currency == selectedCurrency,
                onClick = { onCurrencySelected(currency) }
            )
        }
    }
}

/**
 * Formats a number string with commas every 3 digits before the decimal point
 * Example: "1234.56" -> "1,234.56", "1234567" -> "1,234,567"
 */
private fun formatNumberWithCommas(numberString: String): String {
    if (numberString.isEmpty()) return ""
    
    return try {
        val decimal = BigDecimal(numberString)
        val formatter = DecimalFormat("#,##0.########")
        formatter.format(decimal)
    } catch (e: Exception) {
        // If parsing fails, try to add commas manually for simple integer cases
        if (numberString.contains(".")) {
            val parts = numberString.split(".")
            val integerPart = parts[0]
            val decimalPart = parts[1]
            "${addCommasToInteger(integerPart)}.$decimalPart"
        } else {
            addCommasToInteger(numberString)
        }
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

/**
 * Formats exchange rate with commas every 3 digits before decimal point
 */
private fun formatExchangeRate(rate: BigDecimal): String {
    return formatNumberWithCommas(rate.toPlainString())
}
