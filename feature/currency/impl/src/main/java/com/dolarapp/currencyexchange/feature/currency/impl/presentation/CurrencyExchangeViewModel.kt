package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.lifecycle.viewModelScope
import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider
import com.dolarapp.currencyexchange.core.mvi.BaseMviViewModel
import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    override fun handleIntent(intent: CurrencyExchangeIntent) {
        when (intent) {
            is CurrencyExchangeIntent.LoadCurrencies -> loadCurrencies(intent.useFake)
            is CurrencyExchangeIntent.SelectCurrency -> selectCurrency(intent.currency)
            is CurrencyExchangeIntent.LoadTickers -> loadTickers()
        }
    }

    private fun loadCurrencies(useFake: Boolean) {
        updateState { copy(isLoading = true, error = null) }

        val flow = if (useFake) {
            currencyRepository.getAvailableCurrenciesFake()
        } else {
            currencyRepository.getAvailableCurrencies()
        }

        flow
            .onEach { currencies ->
                updateState {
                    copy(
                        isLoading = false,
                        currencies = currencies,
                        error = null
                    )
                }
            }
            .catch { e ->
                updateState {
                    copy(
                        isLoading = false,
                        error = "Failed to load currencies: ${e.message}"
                    )
                }
                emitEffect(CurrencyExchangeEffect.ShowError("Failed to load currencies: ${e.message}"))
            }
            .launchIn(viewModelScope)
    }

    private fun selectCurrency(currency: String) {
        val currentSelected = currentState.selectedCurrencies.toMutableList()
        if (currency in currentSelected) {
            currentSelected.remove(currency)
        } else {
            currentSelected.add(currency)
        }
        updateState {
            copy(selectedCurrencies = currentSelected)
        }
    }

    private fun loadTickers() {
        val selected = currentState.selectedCurrencies
        if (selected.isEmpty()) {
            updateState { copy(tickers = emptyList()) }
            return
        }

        updateState { copy(isLoading = true, error = null) }

        currencyRepository.getTickers(selected)
            .onEach { tickers ->
                updateState {
                    copy(
                        isLoading = false,
                        tickers = tickers,
                        error = null
                    )
                }
            }
            .catch { e ->
                updateState {
                    copy(
                        isLoading = false,
                        error = "Failed to load tickers: ${e.message}"
                    )
                }
                emitEffect(CurrencyExchangeEffect.ShowError("Failed to load tickers: ${e.message}"))
            }
            .launchIn(viewModelScope)
    }
}

