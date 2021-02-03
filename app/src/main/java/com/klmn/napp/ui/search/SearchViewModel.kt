package com.klmn.napp.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableStateFlow(listOf<Product>())
    val products get() = _products.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading.asStateFlow()

    private var page = 1
    private var query = ""

    companion object { const val PAGE_SIZE = 20 }

    fun search(query: String) {
        this.query = query
        resetProducts()
        updateProducts()
    }

    fun onScroll(pos: Int) {
        if (!loading.value && pos + 1 >= page * PAGE_SIZE) nextPage()
    }

    private fun resetProducts() {
        page = 1
        _products.value = listOf()
    }

    private fun nextPage() {
        page++
        updateProducts()
    }

    private fun updateProducts() = viewModelScope.launch {
        println("update products (page $page)")
        delay(3000)
        _products.value += repository.getProducts(query, page, PAGE_SIZE)
    }
}