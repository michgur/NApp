package com.klmn.napp.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Category
import com.klmn.napp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableStateFlow(listOf<Product>())
    val products get() = _products.asStateFlow()

    private val _categories = MutableStateFlow(listOf<Category>())
    val categories get() = _categories.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading.asStateFlow()

    companion object { const val PAGE_SIZE = 20 }

    init {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
        viewModelScope.launch {
            _products.value = repository.getProducts("", 1)
        }
    }
}