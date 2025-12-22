package com.dolarapp.currencyexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dolarapp.currencyexchange.core.designsystem.theme.CurrencyExchangeTheme
import com.dolarapp.currencyexchange.feature.currency.api.CURRENCY_EXCHANGE_ROUTE
import com.dolarapp.currencyexchange.feature.currency.api.currencyExchangeScreen
import com.dolarapp.currencyexchange.feature.currency.impl.CurrencyExchangeScreenProvider
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for Currency Exchange app
 * Sets up Navigation Compose and applies design system theme
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyExchangeTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = CURRENCY_EXCHANGE_ROUTE,
                    modifier = Modifier.fillMaxSize()
                ) {
                    currencyExchangeScreen {
                        CurrencyExchangeScreenProvider()
                    }
                }
            }
        }
    }
}