package com.klmn.napp.ui.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Category
import com.klmn.napp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object { const val TAG = "HomeViewModel" }

    private val _products = MutableStateFlow<List<Product>?>(null)
    val products get() = _products.filterNotNull()

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories get() = _categories.filterNotNull()

    private var _errors = MutableStateFlow<Exception?>(null)
    val errors get() = _errors.filterNotNull()

    init {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
                _errors.value = e
            }
        }
        viewModelScope.launch {
            try {
                _products.emitAll(repository.getFavoriteProducts())
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
                _errors.value = e
            }
        }
    }
}