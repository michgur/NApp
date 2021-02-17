package com.klmn.napp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var productId: Long = 0L
        set(value) {
            field = value
            if (value != 0L) updateProduct(value)
        }

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.filterNotNull()

    private val _favorite = MutableStateFlow(false)
    val favorite = _favorite.asStateFlow()

    private fun updateProduct(id: Long) = viewModelScope.launch {
        val product = repository.findProductById(id)
        _product.value = product
        _favorite.value = product.favorite
    }

    fun toggleFavorite() {
        _favorite.value = !_favorite.value
        viewModelScope.launch {
            repository.favoriteProduct(productId, _favorite.value)
        }
    }
}