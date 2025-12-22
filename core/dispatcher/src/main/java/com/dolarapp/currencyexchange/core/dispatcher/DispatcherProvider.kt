package com.dolarapp.currencyexchange.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Abstraction for coroutine dispatchers
 * Allows for test-friendly implementations and easy swapping of dispatchers
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * Default implementation using standard coroutine dispatchers
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Main
    
    override val io: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.IO
    
    override val default: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Default
}

