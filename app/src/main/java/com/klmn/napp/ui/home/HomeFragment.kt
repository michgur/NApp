package com.klmn.napp.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.text.method.Touch
import android.view.TouchDelegate
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.klmn.napp.R
import com.klmn.napp.data.network.OpenFoodFactsAPI.Criteria.CATEGORIES
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutCategoryBinding
import com.klmn.napp.model.Category
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.productListAdapter
import com.klmn.napp.ui.components.setup
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.diffCallback
import com.klmn.napp.util.listAdapter
import com.klmn.napp.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : ViewBoundFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        categoriesRecyclerView.adapter = categoryAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect { products ->
                progressBar.isVisible = false
                productAdapter.submitList(products)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.categories.collect(categoryAdapter::submitList)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.errors.collect { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // fill categoriesRecyclerView w/ some placeholder views until categories are loaded
        categoryAdapter.submitList((1..10).map { Category("", PLACEHOLDER_PREFIX + it) })

        toolbar.setup(onSearch = ::onSearch, onScan = ::onScan)
    }

    private fun onScan() = findNavController().navigate(
        HomeFragmentDirections.actionHomeFragmentToScannerFragment()
    )

    private fun onSearch(query: String) {
        HomeFragmentDirections.actionHomeFragmentToSearchFragment(
            query
        ).let(findNavController()::navigate)

        binding.toolbar.searchEditText.setText("")
    }

    private fun onCategoryClick(category: Category) {
        HomeFragmentDirections.actionHomeFragmentToSearchFragment(
            filters = arrayOf(Filter(CATEGORIES, category.id))
        ).let { findNavController().navigate(it) }
    }

    companion object {
        private const val PLACEHOLDER_PREFIX = "placeholder_"
    }

    private val productAdapter = productListAdapter()
    private val categoryAdapter = listAdapter(
        diffCallback { it.name },
        LayoutCategoryBinding::inflate
    ) { category: Category ->
        if (category.name.startsWith(PLACEHOLDER_PREFIX)) return@listAdapter

        textBackgroundView.isVisible = true
        nameTextView.text = category.name
        category.imageURL?.let { url ->
            loadImage(url, R.drawable.ic_product, imageView)
        }
        root.setOnClickListener {
            onCategoryClick(category)
        }
    }
}