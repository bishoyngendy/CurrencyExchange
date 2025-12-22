package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dolarapp.currencyexchange.core.designsystem.components.CurrencyButton
import com.dolarapp.currencyexchange.core.designsystem.components.ErrorState
import com.dolarapp.currencyexchange.core.designsystem.components.LoadingIndicator
import com.dolarapp.currencyexchange.core.designsystem.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

/**
 * Currency Exchange screen composable
 */
@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var useFakeApi by remember { mutableStateOf(false) }

    // Load currencies on initial composition
    LaunchedEffect(Unit) {
        viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadCurrencies(useFake = false))
    }

    // Reload when fake API toggle changes
    LaunchedEffect(useFakeApi) {
        viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadCurrencies(useFakeApi))
    }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CurrencyExchangeEffect.ShowError -> {
                    // Error is already shown in state
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.currencies.isEmpty()) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Title
                Text(
                    text = "Currency Exchange",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Fake API toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Use Fake API",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = useFakeApi,
                        onCheckedChange = { useFakeApi = it }
                    )
                }

                // Load Tickers button
                CurrencyButton(
                    text = "Load Tickers",
                    onClick = { viewModel.processIntentOnMain(CurrencyExchangeIntent.LoadTickers) },
                    enabled = state.selectedCurrencies.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Available currencies
                Text(
                    text = "Available Currencies",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                state.currencies.forEach { currency ->
                    CurrencyCheckbox(
                        currency = currency,
                        isSelected = currency in state.selectedCurrencies,
                        onCheckedChange = {
                            viewModel.processIntentOnMain(CurrencyExchangeIntent.SelectCurrency(currency))
                        }
                    )
                }

                // Tickers list
                if (state.tickers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Exchange Rates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    state.tickers.forEach { ticker ->
                        TickerCard(ticker = ticker)
                    }
                }

                // Error state
                state.error?.let { error ->
                    ErrorState(message = error)
                }
            }
        }
    }
}

@Composable
private fun CurrencyCheckbox(
    currency: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = currency,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = Spacing.sm)
        )
    }
}

@Composable
private fun TickerCard(ticker: com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Text(
                text = ticker.currencyCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Bid",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatBigDecimal(ticker.bid),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column {
                    Text(
                        text = "Ask",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatBigDecimal(ticker.ask),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Updated: ${formatInstant(ticker.lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatBigDecimal(value: BigDecimal): String {
    return value.toPlainString()
}

private fun formatInstant(instant: java.time.Instant): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        .format(formatter)
}
