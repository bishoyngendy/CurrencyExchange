package com.dolarapp.currencyexchange.feature.currency.domain.repository

import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository
import com.dolarapp.currencyexchange.feature.currency.data.api.CurrencyApi
import com.dolarapp.currencyexchange.feature.currency.domain.mapper.CurrencyTickerMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Repository implementation for currency exchange data
 * Handles real API calls with graceful fallbacks
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi
) : CurrencyRepository {

    companion object {
        private val FALLBACK_CURRENCIES = listOf("MXN", "ARS", "BRL", "COP")
    }

    override fun getAvailableCurrencies(): Flow<List<String>> = flow {
        try {
            val currencies = currencyApi.getAvailableCurrencies()
            emit(currencies)
        } catch (e: Exception) {
            // Any other error (network, parsing, etc.)
            emit(FALLBACK_CURRENCIES)
        }
    }

    override fun getAvailableCurrenciesFake(): Flow<List<String>> {
        return flowOf(FALLBACK_CURRENCIES)
    }

    override fun getTickers(currencies: List<String>): Flow<List<CurrencyTicker>> = flow {
        if (currencies.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        try {
            // Join currencies with comma for API query parameter
            val currenciesParam = currencies.joinToString(",")
            val dtos = currencyApi.getTickers(currenciesParam)
            val domain = CurrencyTickerMapper.toDomain(dtos)
            emit(domain)
        } catch (e: Exception) {
            // Emit empty list on error (could also emit error state)
            emit(emptyList())
        }
    }
}

