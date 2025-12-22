package com.dolarapp.currencyexchange.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI architecture
 * 
 * @param State The UI state type implementing UiState
 * @param Intent The user intent type implementing UiIntent
 * @param Effect The side effect type implementing UiEffect
 * 
 * Provides:
 * - StateFlow for state management
 * - SharedFlow for one-off effects
 * - Intent handling with reducer pattern
 */
abstract class BaseMviViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State,
    protected val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effect = MutableSharedFlow<Effect>(replay = 0, extraBufferCapacity = 1)
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    /**
     * Current state value (read-only)
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Update state using reducer pattern
     */
    protected fun updateState(update: State.() -> State) {
        _state.value = _state.value.update()
    }

    /**
     * Emit a one-off effect
     */
    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch(dispatcherProvider.main) {
            _effect.emit(effect)
        }
    }

    /**
     * Handle user intents
     * Override this method to process intents and update state/emit effects
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * Process intent on IO dispatcher (for network/disk operations)
     */
    fun processIntent(intent: Intent) {
        viewModelScope.launch(dispatcherProvider.io) {
            handleIntent(intent)
        }
    }

    /**
     * Process intent on main dispatcher (for UI-only operations)
     */
    fun processIntentOnMain(intent: Intent) {
        viewModelScope.launch(dispatcherProvider.main) {
            handleIntent(intent)
        }
    }
}

