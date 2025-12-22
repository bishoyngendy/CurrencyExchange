package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.lifecycle.viewModelScope
import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider
import com.dolarapp.currencyexchange.core.mvi.BaseMviViewModel
import com.dolarapp.currencyexchange.feature.currency.impl.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Currency Exchange screen
 * Implements MVI pattern using BaseMviViewModel
 */
@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val currencyRepository: CurrencyRepository
) : BaseMviViewModel<CurrencyExchangeState, CurrencyExchangeIntent, CurrencyExchangeEffect>(
    initialState = CurrencyExchangeState(),
    dispatcherProvider = dispatcherProvider
) {

    init {
        // Load initial data
        processIntent(CurrencyExchangeIntent.LoadRates)
    }

    override fun handleIntent(intent: CurrencyExchangeIntent) {
        when (intent) {
            is CurrencyExchangeIntent.LoadRates -> loadRates()
            is CurrencyExchangeIntent.ConvertCurrency -> convertCurrency()
            is CurrencyExchangeIntent.SwapCurrencies -> swapCurrencies()
            is CurrencyExchangeIntent.SelectFromCurrency -> selectFromCurrency(intent.currency)
            is CurrencyExchangeIntent.SelectToCurrency -> selectToCurrency(intent.currency)
            is CurrencyExchangeIntent.UpdateAmount -> updateAmount(intent.amount)
        }
    }

    private fun loadRates() {
        viewModelScope.launch(dispatcherProvider.io) {
            updateState { copy(isLoading = true, error = null) }
            
            try {
                val currencies = currencyRepository.getAvailableCurrencies()
                val rates = currencyRepository.getExchangeRates(currentState.fromCurrency)
                
                updateState {
                    copy(
                        isLoading = false,
                        availableCurrencies = currencies,
                        exchangeRates = rates
                    )
                }
                
                // Auto-convert if amount is already entered
                if (currentState.amount.isNotEmpty()) {
                    convertCurrency()
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
                emitEffect(CurrencyExchangeEffect.ShowError("Failed to load exchange rates: ${e.message}"))
            }
        }
    }

    private fun convertCurrency() {
        viewModelScope.launch(dispatcherProvider.io) {
            val amountText = currentState.amount
            if (amountText.isEmpty() || amountText.toDoubleOrNull() == null) {
                updateState { copy(convertedAmount = "") }
                return@launch
            }

            try {
                val amount = amountText.toDouble()
                val converted = currencyRepository.convertCurrency(
                    amount = amount,
                    fromCurrency = currentState.fromCurrency,
                    toCurrency = currentState.toCurrency
                )
                
                updateState {
                    copy(
                        convertedAmount = String.format("%.2f", converted),
                        error = null
                    )
                }
            } catch (e: Exception) {
                updateState { copy(error = e.message) }
                emitEffect(CurrencyExchangeEffect.ShowError("Conversion failed: ${e.message}"))
            }
        }
    }

    private fun swapCurrencies() {
        updateState {
            copy(
                fromCurrency = toCurrency,
                toCurrency = fromCurrency,
                convertedAmount = ""
            )
        }
        // Reload rates for new base currency and convert
        processIntent(CurrencyExchangeIntent.LoadRates)
    }

    private fun selectFromCurrency(currency: String) {
        updateState {
            copy(
                fromCurrency = currency,
                convertedAmount = ""
            )
        }
        // Reload rates for new base currency and convert
        processIntent(CurrencyExchangeIntent.LoadRates)
    }

    private fun selectToCurrency(currency: String) {
        updateState {
            copy(
                toCurrency = currency,
                convertedAmount = ""
            )
        }
        // Convert with new target currency
        processIntent(CurrencyExchangeIntent.ConvertCurrency)
    }

    private fun updateAmount(amount: String) {
        updateState { copy(amount = amount) }
        // Auto-convert when amount changes
        processIntent(CurrencyExchangeIntent.ConvertCurrency)
    }
}

