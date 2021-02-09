package com.klmn.napp.ui.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Filter
import com.klmn.napp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val TAG = "searchViewModel"
        const val PAGE_SIZE = 20
    }

    private val _products = MutableStateFlow(listOf<Product>())
    val products get() = _products.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading.asStateFlow()

    private var _filters = MutableStateFlow(listOf<Filter>())
    val filters get() = _filters.asStateFlow()

    private var _errors = MutableStateFlow<Exception?>(null)
    val errors get() = _errors.filterNotNull()

    private var page = 1
    private var lastPage = false
    private var query = ""

    fun addFilter(filter: Filter) {
        _filters.value += filter
        clearProducts()
        updateProducts()
    }

    fun removeFilter(filter: Filter) {
        _filters.value -= filter
        clearProducts()
        updateProducts()
    }

    fun search(query: String? = null, filters: List<Filter>? = null) {
        query?.let { this.query = it }
        filters?.let { _filters.value = it }
        clearProducts()
        updateProducts()
    }

    fun onScroll(pos: Int) {
        if (
            !lastPage &&
            !loading.value &&
            pos + 1 >= products.value.size
        ) nextPage()
    }

    private fun clearProducts() {
        Log.d(TAG, "clearing products")
        page = 1
        _products.value = listOf()
        lastPage = false
    }

    private fun nextPage() {
        page++
        updateProducts()
    }

    private fun updateProducts() = viewModelScope.launch {
        try {
            Log.d(TAG, "fetching products (query=$query, page=$page, filters=${filters.value})")
            _loading.value = true
            repository.getProducts(query, page, PAGE_SIZE, filters.value).let { products ->
                if (products.isEmpty()) lastPage = true
                else _products.value += products
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            _errors.value = e
        } finally {
            _loading.value = false
        }
    }
}