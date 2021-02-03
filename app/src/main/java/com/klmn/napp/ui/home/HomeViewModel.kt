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
import kotlinx.coroutines.flow.map
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

    private var page = 1
    private var query = ""

    init {
        viewModelScope.launch {
            _products.value = repository.getProducts(query, page)
            _categories.value = repository.getCategories()
        }
    }

    fun search(query: String) {
        this.query = query
        page = 1

        updateProducts()
    }

    fun nextPage() {
        page++
        updateProducts()
    }

    private fun updateProducts() = viewModelScope.launch {
        _products.value = repository.getProducts(query, page)
    }
}