package com.klmn.napp.ui.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var barcode = 0L
        set(value) {
            field = value
            lookupBarcode()
        }

    var flashEnabled = false

    private val _state = MutableStateFlow(State.SCANNING)
    val state get() = _state.asStateFlow()

    private var _errors = MutableStateFlow<Exception?>(null)
    val errors get() = _errors.filterNotNull()

    private fun lookupBarcode() = viewModelScope.launch {
        try {
            _state.value = State.LOOKUP
            repository.findProductById(barcode)
            _state.value = State.SUCCESS
        } catch (e: Exception) {
            _errors.value = e
            _state.value = State.SCANNING
        }
    }

    enum class State {
        SCANNING,
        LOOKUP,
        SUCCESS
    }
}