package com.dolarapp.currencyexchange.feature.currency.impl.presentation

import app.cash.turbine.test
import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider
import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyExchangeViewModelTest {

    private lateinit var repository: CurrencyRepository
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: CurrencyExchangeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        // Mock repository to return empty flows by default to avoid loading tickers
        coEvery { repository.getTickers(any()) } returns flowOf(emptyList())
        dispatcherProvider = object : DispatcherProvider {
            override val main = testDispatcher
            override val io = testDispatcher
            override val default = testDispatcher
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test SetFromCurrency - changing base currency to USDc`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                baseAmount = "100",
                quoteAmount = "4.88",
                isConvertingFromUSDc = false,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromCurrency("USDc"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("USDc", state.calculator.baseCurrency)
            assertEquals("MXN", state.calculator.quoteCurrency)
            assertTrue(state.calculator.isConvertingFromUSDc)
            // USDc amount should be preserved (was in quote, now in base)
            assertEquals("4.88", state.calculator.baseAmount)
            // MXN amount should be recalculated: USDc * BID = 4.88 * 20.50 = 100.04
            assertEquals("100.04", state.calculator.quoteAmount)
            assertEquals(BigDecimal("20.50"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetFromCurrency - changing base currency from USDc to MXN`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromCurrency("MXN"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("MXN", state.calculator.baseCurrency)
            assertEquals("USDc", state.calculator.quoteCurrency)
            assertFalse(state.calculator.isConvertingFromUSDc)
            // USDc amount should be preserved (was in base, now in quote)
            assertEquals("100", state.calculator.quoteAmount)
            // MXN amount should be recalculated: USDc * ASK = 100 * 20.60 = 2060.00
            assertEquals("2060.00", state.calculator.baseAmount)
            assertEquals(BigDecimal("20.60"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetToCurrency - changing quote currency to USDc`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToCurrency("USDc"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("MXN", state.calculator.baseCurrency)
            assertEquals("USDc", state.calculator.quoteCurrency)
            assertFalse(state.calculator.isConvertingFromUSDc)
            // USDc amount should be preserved (was in base, now in quote)
            assertEquals("100", state.calculator.quoteAmount)
            // MXN amount should be recalculated: USDc * ASK = 100 * 20.60 = 2060.00
            assertEquals("2060.00", state.calculator.baseAmount)
            assertEquals(BigDecimal("20.60"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetToCurrency - changing quote currency from USDc to MXN`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                baseAmount = "2060.00",
                quoteAmount = "100",
                isConvertingFromUSDc = false,
                rate = BigDecimal("20.60")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToCurrency("MXN"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("USDc", state.calculator.baseCurrency)
            assertEquals("MXN", state.calculator.quoteCurrency)
            assertTrue(state.calculator.isConvertingFromUSDc)
            // USDc amount should be preserved (was in quote, now in base)
            assertEquals("100", state.calculator.baseAmount)
            // MXN amount should be recalculated: USDc * BID = 100 * 20.50 = 2050.00
            assertEquals("2050.00", state.calculator.quoteAmount)
            assertEquals(BigDecimal("20.50"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetFromAmount - converting from USDc to MXN using BID rate`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromAmount("200"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("200", state.calculator.baseAmount)
            // MXN = USDc * BID = 200 * 20.50 = 4100.00
            assertEquals("4100.00", state.calculator.quoteAmount)
            assertEquals(BigDecimal("20.50"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetFromAmount - converting from MXN to USDc using ASK rate`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                baseAmount = "2060.00",
                quoteAmount = "100",
                isConvertingFromUSDc = false,
                rate = BigDecimal("20.60")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromAmount("4120.00"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("4120.00", state.calculator.baseAmount)
            // USDc = MXN / ASK = 4120.00 / 20.60 = 200.00 (exact division)
            assertEquals("200.00", state.calculator.quoteAmount)
            assertEquals(BigDecimal("20.60"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetToAmount - converting from USDc to MXN using BID rate`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToAmount("4100.00"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("4100.00", state.calculator.quoteAmount)
            // USDc = MXN / BID = 4100.00 / 20.50 = 200.00
            assertEquals("200.00", state.calculator.baseAmount)
            assertEquals(BigDecimal("20.50"), state.calculator.rate)
        }
    }

    @Test
    fun `test SetToAmount - converting from MXN to USDc using ASK rate`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                baseAmount = "2060.00",
                quoteAmount = "100",
                isConvertingFromUSDc = false,
                rate = BigDecimal("20.60")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToAmount("200"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("200", state.calculator.quoteAmount)
            // MXN = USDc * ASK = 200 * 20.60 = 4120.00
            assertEquals("4120.00", state.calculator.baseAmount)
            assertEquals(BigDecimal("20.60"), state.calculator.rate)
        }
    }

    @Test
    fun `test SwapCurrencies - swapping from USDc to MXN`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SwapCurrencies)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            // Currencies should be swapped
            assertEquals("MXN", state.calculator.baseCurrency)
            assertEquals("USDc", state.calculator.quoteCurrency)
            // isConvertingFromUSDc should be flipped
            assertFalse(state.calculator.isConvertingFromUSDc)
            // USDc amount should move from base to quote
            assertEquals("100", state.calculator.quoteAmount)
            // MXN amount should be recalculated: USDc * ASK = 100 * 20.60 = 2060.00
            assertEquals("2060.00", state.calculator.baseAmount)
            // Rate should change from BID to ASK
            assertEquals(BigDecimal("20.60"), state.calculator.rate)
        }
    }

    @Test
    fun `test SwapCurrencies - swapping from MXN to USDc`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                baseAmount = "2060.00",
                quoteAmount = "100",
                isConvertingFromUSDc = false,
                rate = BigDecimal("20.60")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SwapCurrencies)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            // Currencies should be swapped
            assertEquals("USDc", state.calculator.baseCurrency)
            assertEquals("MXN", state.calculator.quoteCurrency)
            // isConvertingFromUSDc should be flipped
            assertTrue(state.calculator.isConvertingFromUSDc)
            // USDc amount should move from quote to base
            assertEquals("100", state.calculator.baseAmount)
            // MXN amount should be recalculated: USDc * BID = 100 * 20.50 = 2050.00
            assertEquals("2050.00", state.calculator.quoteAmount)
            // Rate should change from ASK to BID
            assertEquals(BigDecimal("20.50"), state.calculator.rate)
        }
    }

    @Test
    fun `test SwapCurrencies - preserves USDc amount correctly`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "50.5",
                quoteAmount = "1035.25",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SwapCurrencies)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            // USDc amount should be preserved (was in base, now in quote)
            assertEquals("50.5", state.calculator.quoteAmount)
            // MXN should be recalculated: USDc * ASK = 50.5 * 20.60 = 1040.30
            assertEquals("1040.30", state.calculator.baseAmount)
        }
    }

    @Test
    fun `test computeDisplayExchangeRate - when converting from USDc`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                isConvertingFromUSDc = true
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        val result = viewModel.computeDisplayExchangeRate()

        // Then
        assertNotNull(result)
        assertEquals(BigDecimal("20.50"), result?.first) // Should use BID
        assertEquals("MXN", result?.second)
    }

    @Test
    fun `test computeDisplayExchangeRate - when converting to USDc`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                isConvertingFromUSDc = false
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        val result = viewModel.computeDisplayExchangeRate()

        // Then
        assertNotNull(result)
        assertEquals(BigDecimal("20.60"), result?.first) // Should use ASK
        assertEquals("MXN", result?.second)
    }

    @Test
    fun `test computeDisplayExchangeRate - returns null when ticker not available`() = runTest {
        // Given
        val initialState = CurrencyExchangeState(
            tickers = emptyMap(),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                isConvertingFromUSDc = true
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        val result = viewModel.computeDisplayExchangeRate()

        // Then
        assertNull(result)
    }

    @Test
    fun `test SetFromAmount - empty amount clears quote amount`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetFromAmount(""))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.calculator.baseAmount)
            assertEquals("", state.calculator.quoteAmount)
        }
    }

    @Test
    fun `test SetToAmount - empty amount clears base amount`() = runTest {
        // Given
        val mxnTicker = createTicker("MXN", bid = BigDecimal("20.50"), ask = BigDecimal("20.60"))
        val initialState = CurrencyExchangeState(
            tickers = mapOf("MXN" to mxnTicker),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
        viewModel = CurrencyExchangeViewModel(dispatcherProvider, repository)
        viewModel.setTestState(initialState)

        // When
        viewModel.processIntentOnMain(CurrencyExchangeIntent.SetToAmount(""))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.calculator.baseAmount)
            assertEquals("", state.calculator.quoteAmount)
        }
    }

    // Helper function to create CurrencyTicker
    private fun createTicker(
        currencyCode: String,
        bid: BigDecimal,
        ask: BigDecimal,
        lastUpdated: Instant = Instant.now()
    ): CurrencyTicker {
        return CurrencyTicker(
            currencyCode = currencyCode,
            bid = bid,
            ask = ask,
            lastUpdated = lastUpdated
        )
    }
}

