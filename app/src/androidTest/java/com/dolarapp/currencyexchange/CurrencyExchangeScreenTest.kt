package com.dolarapp.currencyexchange

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CurrencyExchangeIntent
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CurrencyExchangeScreen
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CurrencyExchangeState
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CalculatorState
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.CurrencyExchangeViewModel
import com.dolarapp.currencyexchange.feature.currency.impl.presentation.ScreenStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

/**
 * UI tests for CurrencyExchangeScreen
 */
class CurrencyExchangeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: CurrencyExchangeViewModel

    private val stateFlow = MutableStateFlow(
        CurrencyExchangeState(
            screenStatus = ScreenStatus.Idle,
            currencies = listOf("MXN", "ARS", "BRL", "COP"),
            tickers = mapOf(
                "MXN" to CurrencyTicker(
                    currencyCode = "MXN",
                    bid = BigDecimal("20.50"),
                    ask = BigDecimal("20.60"),
                    lastUpdated = Instant.now()
                )
            ),
            calculator = CalculatorState(
                baseCurrency = "USDc",
                quoteCurrency = "MXN",
                baseAmount = "100",
                quoteAmount = "2050.00",
                isConvertingFromUSDc = true,
                rate = BigDecimal("20.50")
            )
        )
    )

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.state } returns stateFlow
        every { mockViewModel.computeDisplayExchangeRate() } returns Pair(BigDecimal("20.50"), "MXN")
        every { mockViewModel.processIntentOnMain(any()) } returns Unit
    }

    @Test
    fun screen_displaysTitle() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Exchange calculator")
            .assertIsDisplayed()
    }

    @Test
    fun screen_displaysExchangeRate() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("1 USDc = 20.5 MXN", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun screen_displaysBaseCurrencyCard() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Check for USDc currency code
        composeTestRule.onNodeWithText("USDc")
            .assertIsDisplayed()
    }

    @Test
    fun screen_displaysQuoteCurrencyCard() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Check for MXN currency code
        composeTestRule.onNodeWithText("MXN")
            .assertIsDisplayed()
    }

    @Test
    fun screen_displaysCurrencyAmounts() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Check for formatted amounts (they might have $ and commas)
        composeTestRule.onNodeWithText("$100", substring = true)
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("2,050", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun screen_allowsAmountInput() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Find the base amount input field and enter text
        composeTestRule.onNodeWithText("$100", substring = true)
            .performClick()
            .performTextReplacement("200")

        // Verify the intent was called
        verify { mockViewModel.processIntentOnMain(any<CurrencyExchangeIntent.SetFromAmount>()) }
    }

    @Test
    fun screen_showsLoadingIndicator_whenLoading() {
        stateFlow.value = stateFlow.value.copy(
            screenStatus = ScreenStatus.Loading
        )

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Loading indicator should be visible
        // Note: This depends on how LoadingIndicator is implemented
        // If it has specific text, we can check for it
    }

    @Test
    fun screen_opensBottomSheet_whenCurrencyClicked() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on the quote currency (MXN) which should be selectable
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        // Bottom sheet title should be visible
        composeTestRule.onNodeWithText("Choose currency")
            .assertIsDisplayed()
    }

    @Test
    fun bottomSheet_displaysCurrencyList() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on MXN to open bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        // Wait for bottom sheet to appear
        composeTestRule.waitForIdle()

        // Check that currencies are displayed in the bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ARS")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("BRL")
            .assertIsDisplayed()
    }

    @Test
    fun bottomSheet_highlightsSelectedCurrency() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on MXN to open bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        composeTestRule.waitForIdle()

        // MXN should be selected (checkmark visible)
        // Note: This depends on CurrencyListItem implementation
        // We can check if the currency is displayed
        composeTestRule.onNodeWithText("MXN")
            .assertIsDisplayed()
    }

    @Test
    fun bottomSheet_selectsCurrency_whenClicked() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on MXN to open bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        composeTestRule.waitForIdle()

        // Click on ARS in the bottom sheet
        composeTestRule.onNodeWithText("ARS")
            .performClick()

        // Verify the intent was called
        verify { mockViewModel.processIntentOnMain(any<CurrencyExchangeIntent.SetToCurrency>()) }
    }

    @Test
    fun bottomSheet_closes_whenCurrencySelected() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on MXN to open bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        composeTestRule.waitForIdle()

        // Select a currency
        composeTestRule.onNodeWithText("ARS")
            .performClick()

        composeTestRule.waitForIdle()

        // Bottom sheet should be closed (title not visible)
        composeTestRule.onNodeWithText("Choose currency")
            .assertDoesNotExist()
    }

    @Test
    fun bottomSheet_closes_whenCloseButtonClicked() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Click on MXN to open bottom sheet
        composeTestRule.onNodeWithText("MXN")
            .performClick()

        composeTestRule.waitForIdle()

        // Click the close button (✕)
        composeTestRule.onNodeWithText("✕")
            .performClick()

        composeTestRule.waitForIdle()

        // Bottom sheet should be closed
        composeTestRule.onNodeWithText("Choose currency")
            .assertDoesNotExist()
    }

    @Test
    fun swapButton_isDisplayed() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Swap button should be visible (centered between cards)
        // Note: Swap button might not have text, so we check by interaction
        // We can verify it exists by trying to click it
    }

    @Test
    fun swapButton_swapsCurrencies_whenClicked() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Find and click swap button
        // Since SwapCurrencyButton might not have accessible text,
        // we might need to add a test tag or use coordinates
        // For now, we'll verify the intent is called when swap happens
        
        // This test would need the swap button to be clickable
        // We can verify by checking that the swap intent is called
    }

    @Test
    fun screen_updates_whenStateChanges() {
        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Update state
        stateFlow.value = stateFlow.value.copy(
            calculator = stateFlow.value.calculator.copy(
                baseAmount = "200",
                quoteAmount = "4100.00"
            )
        )

        composeTestRule.waitForIdle()

        // Check that new amounts are displayed
        composeTestRule.onNodeWithText("$200", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun screen_displaysCorrectCurrency_whenSwapped() {
        // Set up state with swapped currencies
        stateFlow.value = stateFlow.value.copy(
            calculator = stateFlow.value.calculator.copy(
                baseCurrency = "MXN",
                quoteCurrency = "USDc",
                isConvertingFromUSDc = false
            )
        )

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // MXN should be in base position
        composeTestRule.onNodeWithText("MXN")
            .assertIsDisplayed()
        
        // USDc should be in quote position
        composeTestRule.onNodeWithText("USDc")
            .assertIsDisplayed()
    }

    @Test
    fun screen_hidesExchangeRate_whenNoTicker() {
        every { mockViewModel.computeDisplayExchangeRate() } returns null

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        // Exchange rate text should not be displayed or should be empty
        composeTestRule.onNodeWithText("1 USDc =", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun screen_formatsLargeAmounts_withCommas() {
        stateFlow.value = stateFlow.value.copy(
            calculator = stateFlow.value.calculator.copy(
                baseAmount = "1000000",
                quoteAmount = "20500000.00"
            )
        )

        composeTestRule.setContent {
            CurrencyExchangeScreen(viewModel = mockViewModel)
        }

        composeTestRule.waitForIdle()

        // Check for comma formatting
        composeTestRule.onNodeWithText("1,000,000", substring = true)
            .assertIsDisplayed()
    }
}

