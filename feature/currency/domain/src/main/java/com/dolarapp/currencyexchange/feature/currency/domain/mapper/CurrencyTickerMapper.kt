package com.dolarapp.currencyexchange.feature.currency.domain.mapper

import com.dolarapp.currencyexchange.feature.currency.api.domain.CurrencyTicker
import com.dolarapp.currencyexchange.feature.currency.data.dto.CurrencyTickerDto
import java.math.BigDecimal
import java.time.Instant

/**
 * Maps DTO to domain model
 */
object CurrencyTickerMapper {
    /**
     * Extract currency code from book string
     * Example: "usdc_mxn" -> "MXN"
     */
    private fun extractCurrencyCode(book: String): String {
        return book.split("_").lastOrNull()?.uppercase() ?: book.uppercase()
    }

    /**
     * Map DTO to domain model
     */
    fun toDomain(dto: CurrencyTickerDto): CurrencyTicker {
        return CurrencyTicker(
            currencyCode = extractCurrencyCode(dto.book),
            bid = BigDecimal(dto.bid),
            ask = BigDecimal(dto.ask),
            lastUpdated = try {
                Instant.parse(dto.date)
            } catch (e: Exception) {
                Instant.now() // Fallback to current time
            }
        )
    }

    /**
     * Map list of DTOs to domain models
     */
    fun toDomain(dtos: List<CurrencyTickerDto>): List<CurrencyTicker> {
        return dtos.map { toDomain(it) }
    }
}

