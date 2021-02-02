package com.klmn.napp.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmn.napp.data.Repository
import com.klmn.napp.model.Category
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
    val query = MutableStateFlow("")

    val products = query.map {
        repository.getProducts(it)
    }

    private val _categories = MutableStateFlow(listOf<Category>())
    val categories get() = _categories.asStateFlow()

    fun parseCategories(categories: Array<String>) {
        viewModelScope.launch {
            _categories.value = categories.map { c ->
                c.split("|").let {
                    Category(it[0], it[1], repository.getCategoryImageURL(it[1]))
                }
            }
        }
    }
}