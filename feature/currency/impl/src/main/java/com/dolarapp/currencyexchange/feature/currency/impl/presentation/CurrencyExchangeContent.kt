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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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

/**
 * Pure composable for Currency Exchange screen content
 * Takes state and callbacks - easy to test without ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeContent(
    state: CurrencyExchangeState,
    exchangeRateText: String,
    onLoadCurrencies: () -> Unit,
    onSetFromCurrency: (String) -> Unit,
    onSetToCurrency: (String) -> Unit,
    onSetFromAmount: (String) -> Unit,
    onSetToAmount: (String) -> Unit,
    onSwapCurrencies: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }
    var isSelectingFromCurrency by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier
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
                    text = exchangeRateText,
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
                                onSetFromAmount(cleanAmount)
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
                                onSetToAmount(cleanAmount)
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
                            onClick = onSwapCurrencies
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
                    onSetFromCurrency(currency)
                } else {
                    onSetToCurrency(currency)
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

