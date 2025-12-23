package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import androidx.lifecycle.viewModelScope
import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider
import com.dolarapp.currencyexchange.core.mvi.BaseMviViewModel
import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import androidx.annotation.VisibleForTesting

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
            is CurrencyExchangeIntent.LoadCurrencies -> loadCurrencies()
            is CurrencyExchangeIntent.SetFromCurrency -> updateStateForIntent { setFromCurrency(it, intent.currency) }
            is CurrencyExchangeIntent.SetToCurrency -> updateStateForIntent { setToCurrency(it, intent.currency) }
            is CurrencyExchangeIntent.SetFromAmount -> updateStateForIntent { setFromAmount(it, intent.amount) }
            is CurrencyExchangeIntent.SetToAmount -> updateStateForIntent { setToAmount(it, intent.amount) }
            is CurrencyExchangeIntent.SwapCurrencies -> swapCurrencies()
        }
    }
    
    /**
     * Single update state function for all intents (except LoadCurrencies and SwapCurrencies)
     * This ensures consistent state updates across all intents
     */
    private fun updateStateForIntent(updateFn: (CurrencyExchangeState) -> CurrencyExchangeState) {
        val newState = updateFn(currentState)
        updateState { newState }
        
        // Ensure ticker is loaded for the non-USDc currency after state update
        ensureTickerLoaded()
    }
    
    /**
     * Compute the exchange rate based on direction without updating state
     * If isConvertingFromUSDc is true → use bid
     * If isConvertingFromUSDc is false → use ask
     */
    private fun computeRate(
        baseCurrency: String,
        quoteCurrency: String,
        isConvertingFromUSDc: Boolean,
        tickers: Map<String, CurrencyTicker>
    ): BigDecimal? {
        // Find the non-USDc currency (ticker is stored under this key)
        val tickerCurrency = if (isConvertingFromUSDc) {
            quoteCurrency
        } else {
            baseCurrency
        }
        
        val ticker = tickers[tickerCurrency] ?: return null
        
        return if (isConvertingFromUSDc) {
            // Converting from USDc to other currency → use BID
            ticker.bid
        } else {
            // Converting from other currency to USDc → use ASK
            ticker.ask
        }
    }
    
    /**
     * Compute exchange rate for display - always shows "1 USDc = x other currency"
     * x is bid when converting from USDc (isConvertingFromUSDc = true)
     * x is ask when converting to USDc (isConvertingFromUSDc = false)
     */
    fun computeDisplayExchangeRate(): Pair<BigDecimal?, String>? {
        val state = currentState
        // Find the non-USDc currency (ticker is stored under this key)
        val tickerCurrency = if (state.calculator.isConvertingFromUSDc) {
            state.calculator.quoteCurrency
        } else {
            state.calculator.baseCurrency
        }
        
        val ticker = state.tickers[tickerCurrency] ?: return null
        
        return if (state.calculator.isConvertingFromUSDc) {
            Pair(ticker.bid, tickerCurrency)
        } else {
            Pair(ticker.ask, tickerCurrency)
        }
    }

    private fun loadCurrencies() {
        updateState { copy(screenStatus = ScreenStatus.Loading) }

        currencyRepository.getAvailableCurrencies()
            .onEach { currencies ->
                // Filter out USDc and select the first currency
                val availableCurrencies = currencies.filter { it != "USDc" }
                val firstCurrency = availableCurrencies.firstOrNull() ?: ""
                val calculatorState = currentState.calculator
                
                val newQuoteCurrency = firstCurrency.ifEmpty { calculatorState.quoteCurrency }
                val isConvertingFromUSDc = calculatorState.isConvertingFromUSDc
                
                updateState {
                    copy(
                        screenStatus = ScreenStatus.Idle,
                        currencies = currencies,
                        calculator = calculator.copy(
                            quoteCurrency = newQuoteCurrency,
                            isConvertingFromUSDc = isConvertingFromUSDc
                        )
                    )
                }
                
                // Load ticker for the selected currency
                if (firstCurrency.isNotEmpty()) {
                    loadTickerForCurrency(firstCurrency)
                }
            }
            .catch { e ->
                updateState {
                    copy(
                        screenStatus = ScreenStatus.Error("Failed to load currencies: ${e.message}")
                    )
                }
                emitEffect(CurrencyExchangeEffect.ShowError("Failed to load currencies: ${e.message}"))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Handle SetFromCurrency intent
     * When changing base currency:
     * - Update isConvertingFromUSDc based on new base currency
     * - Keep the same USDC amount and recalculate the other currency amount
     */
    private fun setFromCurrency(state: CurrencyExchangeState, currency: String): CurrencyExchangeState {
        val isConvertingFromUSDc = currency == "USDc"
        
        // Get the USDC amount (whichever currency is USDc)
        val usdcAmount = if (state.calculator.isConvertingFromUSDc) {
            state.calculator.baseAmount
        } else {
            state.calculator.quoteAmount
        }
        
        // Determine the new base and quote currencies
        val newBaseCurrency = currency
        val newQuoteCurrency = if (currency == "USDc") {
            // If base becomes USDc, quote should be the old base currency
            state.calculator.baseCurrency
        } else {
            // If base becomes non-USDc, quote should be USDc
            "USDc"
        }
        
        // Compute new rate
        val newRate = computeRate(newBaseCurrency, newQuoteCurrency, isConvertingFromUSDc, state.tickers)
        
        // Recalculate amounts: keep USDC amount, recalculate the other currency
        val (newBaseAmount, newQuoteAmount) = if (isConvertingFromUSDc) {
            // Converting FROM USDc TO X: X = USDc * BID
            // Base is USDc, Quote is X
            Pair(usdcAmount, calculateQuoteAmount(usdcAmount, newRate))
        } else {
            // Converting FROM X TO USDc: X = USDc * ASK (to get X from USDc)
            // Base is X, Quote is USDc
            Pair(calculateQuoteAmount(usdcAmount, newRate), usdcAmount)
        }
        
        return state.copy(
            calculator = state.calculator.copy(
                baseCurrency = newBaseCurrency,
                quoteCurrency = newQuoteCurrency,
                baseAmount = newBaseAmount,
                quoteAmount = newQuoteAmount,
                isConvertingFromUSDc = isConvertingFromUSDc,
                rate = newRate
            )
        )
    }

    /**
     * Handle SetToCurrency intent
     * When changing quote currency:
     * - Keep the same USDC amount and recalculate the other currency amount
     */
    private fun setToCurrency(state: CurrencyExchangeState, currency: String): CurrencyExchangeState {
        // Get the USDC amount (whichever currency is USDc)
        val usdcAmount = if (state.calculator.isConvertingFromUSDc) {
            state.calculator.baseAmount
        } else {
            state.calculator.quoteAmount
        }
        
        // Determine the new base and quote currencies
        val newQuoteCurrency = currency
        val newBaseCurrency = if (currency == "USDc") {
            // If quote becomes USDc, base should be the old quote currency
            state.calculator.quoteCurrency
        } else {
            // If quote becomes non-USDc, base should be USDc
            "USDc"
        }

        // Update isConvertingFromUSDc based on new setup
        val isConvertingFromUSDc = newBaseCurrency == "USDc"
        
        // Get ticker key (non-USDc currency)
        val tickerCurrency = if (isConvertingFromUSDc) {
            currency
        } else {
            newBaseCurrency
        }
        
        // Compute new rate
        val newRate = computeRate(newBaseCurrency, currency, isConvertingFromUSDc, state.tickers)
        
        // Recalculate amounts: keep USDC amount, recalculate the other currency
        val (newBaseAmount, newQuoteAmount) = if (isConvertingFromUSDc) {
            // Converting FROM USDc TO X: X = USDc * BID
            // Base is USDc, Quote is X
            Pair(usdcAmount, calculateQuoteAmount(usdcAmount, newRate))
        } else {
            // Converting FROM X TO USDc: X = USDc * ASK (to get X from USDc)
            // Base is X, Quote is USDc
            Pair(calculateQuoteAmount(usdcAmount, newRate), usdcAmount)
        }
        
        return state.copy(
            calculator = state.calculator.copy(
                baseCurrency = newBaseCurrency,
                quoteCurrency = currency,
                baseAmount = newBaseAmount,
                quoteAmount = newQuoteAmount,
                isConvertingFromUSDc = isConvertingFromUSDc,
                rate = newRate
            )
        )
    }

    /**
     * Handle SetFromAmount intent
     * When changing base amount:
     * - If converting FROM USDc TO X: X = USDc * BID
     * - If converting FROM X TO USDc: USDc = X / ASK
     */
    private fun setFromAmount(state: CurrencyExchangeState, amount: String): CurrencyExchangeState {
        // Recompute rate to ensure it's up to date
        val newRate = computeRate(
            state.calculator.baseCurrency,
            state.calculator.quoteCurrency,
            state.calculator.isConvertingFromUSDc,
            state.tickers
        )
        
        // Recalculate quote amount based on conversion direction
        val newQuoteAmount = if (state.calculator.isConvertingFromUSDc) {
            // Converting FROM USDc TO X: X = USDc * BID
            calculateQuoteAmount(amount, newRate)
        } else {
            // Converting FROM X TO USDc: USDc = X / ASK
            calculateBaseAmount(amount, newRate)
        }
        
        return state.copy(
            calculator = state.calculator.copy(
                baseAmount = amount,
                quoteAmount = newQuoteAmount,
                rate = newRate
            )
        )
    }

    /**
     * Handle SetToAmount intent
     * When changing quote amount:
     * - If converting FROM USDc TO X: USDc = X / BID
     * - If converting FROM X TO USDc: X = USDc * ASK
     */
    private fun setToAmount(state: CurrencyExchangeState, amount: String): CurrencyExchangeState {
        // Recompute rate to ensure it's up to date
        val newRate = computeRate(
            state.calculator.baseCurrency,
            state.calculator.quoteCurrency,
            state.calculator.isConvertingFromUSDc,
            state.tickers
        )
        
        // Recalculate base amount based on conversion direction
        val newBaseAmount = if (state.calculator.isConvertingFromUSDc) {
            // Converting FROM USDc TO X: USDc = X / BID
            calculateBaseAmount(amount, newRate)
        } else {
            // Converting FROM X TO USDc: X = USDc * ASK
            calculateQuoteAmount(amount, newRate)
        }
        
        return state.copy(
            calculator = state.calculator.copy(
                baseAmount = newBaseAmount,
                quoteAmount = amount,
                rate = newRate
            )
        )
    }

    /**
     * Handle SwapCurrencies intent
     * 1. Swap isConvertingFromUSDc (which changes rate from bid to ask or vice versa)
     * 2. Swap base and quote currencies
     * 3. Move USDC amount from base to quote or vice versa
     * 4. Recalculate the other currency amount based on the new rate
     */
    private fun swapCurrencies() {
        val state = currentState
        
        // 1. Swap isConvertingFromUSDc
        val newIsConvertingFromUSDc = !state.calculator.isConvertingFromUSDc
        
        // 2. Swap base and quote currencies
        val newBaseCurrency = state.calculator.quoteCurrency
        val newQuoteCurrency = state.calculator.baseCurrency
        
        // 3. Move USDC amount to its new position
        // If USDc was in base, move baseAmount to quoteAmount
        // If USDc was in quote, move quoteAmount to baseAmount
        val usdcAmount = if (state.calculator.isConvertingFromUSDc) {
            state.calculator.baseAmount
        } else {
            state.calculator.quoteAmount
        }
        
        // 4. Compute new rate with swapped isConvertingFromUSDc
        val newRate = computeRate(newBaseCurrency, newQuoteCurrency, newIsConvertingFromUSDc, state.tickers)
        
        // 5. Recalculate the other currency amount based on new rate
        val (newBaseAmount, newQuoteAmount) = if (newIsConvertingFromUSDc) {
            // Converting FROM USDc TO X: X = USDc * BID
            // Base is USDc, Quote is X
            Pair(usdcAmount, calculateQuoteAmount(usdcAmount, newRate))
        } else {
            // Converting FROM X TO USDc: X = USDc * ASK (to get X from USDc)
            // Base is X, Quote is USDc
            Pair(calculateQuoteAmount(usdcAmount, newRate), usdcAmount)
        }
        
        updateState { 
            copy(
                calculator = calculator.copy(
                    baseCurrency = newBaseCurrency,
                    quoteCurrency = newQuoteCurrency,
                    baseAmount = newBaseAmount,
                    quoteAmount = newQuoteAmount,
                    isConvertingFromUSDc = newIsConvertingFromUSDc,
                    rate = newRate
                )
            )
        }
        // Ensure ticker is loaded for the non-USDc currency
        ensureTickerLoaded()
    }
    
    /**
     * Calculate quote amount from base amount and rate (without updating state)
     * No rounding during calculation - only round at final result
     */
    private fun calculateQuoteAmount(baseAmount: String, rate: BigDecimal?): String {
        if (rate == null || baseAmount.isEmpty()) {
            return ""
        }
        
        return try {
            val baseDecimal = BigDecimal(baseAmount)
            // Multiply without rounding - keep full precision
            val quoteDecimal = baseDecimal.multiply(rate)
            // Round only at the final result
            quoteDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString()
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Calculate base amount from quote amount and rate (without updating state)
     * No rounding during calculation - only round at final result
     */
    private fun calculateBaseAmount(quoteAmount: String, rate: BigDecimal?): String {
        if (rate == null || quoteAmount.isEmpty()) {
            return ""
        }
        
        return try {
            val quoteDecimal = BigDecimal(quoteAmount)
            // Divide with maximum precision (34 digits) to preserve precision during calculation
            // The rounding mode here is only for the division operation, not the final result
            val baseDecimal = quoteDecimal.divide(rate, 34, RoundingMode.HALF_UP)
            // Round only at the final result
            baseDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Ensure ticker is loaded for the non-USDc currency
     * After loading, update the rate and recalculate amounts
     */
    private fun ensureTickerLoaded() {
        val state = currentState
        // Find the non-USDc currency (ticker is stored under this key)
        val tickerCurrency = if (state.calculator.isConvertingFromUSDc) {
            state.calculator.quoteCurrency
        } else {
            state.calculator.baseCurrency
        }
        
        val ticker = state.tickers[tickerCurrency]
        
        if (ticker != null && isTickerValid(ticker)) {
            // Ticker is available and valid, update the rate and recalculate
            val newRate = computeRate(
                state.calculator.baseCurrency,
                state.calculator.quoteCurrency,
                state.calculator.isConvertingFromUSDc,
                state.tickers
            )
            
            // Recalculate amounts based on conversion direction
            val (newBaseAmount, newQuoteAmount) = if (state.calculator.isConvertingFromUSDc) {
                // Converting FROM USDc TO X: X = USDc * BID
                Pair(state.calculator.baseAmount, calculateQuoteAmount(state.calculator.baseAmount, newRate))
            } else {
                // Converting FROM X TO USDc: X = USDc * ASK (to get X from USDc)
                Pair(calculateQuoteAmount(state.calculator.quoteAmount, newRate), state.calculator.quoteAmount)
            }
            
            updateState {
                copy(
                    calculator = calculator.copy(
                        rate = newRate,
                        baseAmount = newBaseAmount,
                        quoteAmount = newQuoteAmount
                    )
                )
            }
        } else {
            // Load ticker if not available or expired
            loadTickerForCurrency(tickerCurrency)
        }
    }
    
    /**
     * Check if ticker is still valid (within 30 seconds TTL)
     */
    private fun isTickerValid(ticker: CurrencyTicker): Boolean {
        val now = Instant.now()
        val age = Duration.between(ticker.lastUpdated, now)
        return age.seconds < 30
    }

    private fun loadTickerForCurrency(currency: String) {
        updateState { copy(screenStatus = ScreenStatus.Loading) }
        
        currencyRepository.getTickers(listOf(currency))
            .onEach { tickers ->
                // Convert List to Map and merge with existing tickers
                val newTickersMap = tickers.associateBy { it.currencyCode }
                val updatedTickers = currentState.tickers + newTickersMap
                
                val state = currentState
                val newRate = computeRate(
                    state.calculator.baseCurrency,
                    state.calculator.quoteCurrency,
                    state.calculator.isConvertingFromUSDc,
                    updatedTickers
                )
                
                // Recalculate amounts based on conversion direction
                val (newBaseAmount, newQuoteAmount) = if (state.calculator.isConvertingFromUSDc) {
                    // Converting FROM USDc TO X: X = USDc * BID
                    Pair(state.calculator.baseAmount, calculateQuoteAmount(state.calculator.baseAmount, newRate))
                } else {
                    // Converting FROM X TO USDc: X = USDc * ASK (to get X from USDc)
                    Pair(calculateQuoteAmount(state.calculator.quoteAmount, newRate), state.calculator.quoteAmount)
                }
                
                updateState { 
                    copy(
                        screenStatus = ScreenStatus.Idle,
                        tickers = updatedTickers,
                        calculator = calculator.copy(
                            rate = newRate,
                            baseAmount = newBaseAmount,
                            quoteAmount = newQuoteAmount
                        )
                    )
                }
            }
            .catch { e ->
                updateState { 
                    copy(
                        screenStatus = ScreenStatus.Error("Failed to load exchange rate: ${e.message}")
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Test helper to set initial state
     * Only used for testing purposes
     */
    @VisibleForTesting
    internal fun setTestState(state: CurrencyExchangeState) {
        updateState { state }
    }
}
