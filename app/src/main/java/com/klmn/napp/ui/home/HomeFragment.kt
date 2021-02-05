package com.klmn.napp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutCategoryBinding
import com.klmn.napp.model.Category
import com.klmn.napp.ui.components.productListAdapter
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
            viewModel.products.collect(productAdapter::submitList)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.categories.collect(categoryAdapter::submitList)
        }

        Unit
    }

    private fun onCategoryClick(category: Category) {
        HomeFragmentDirections.actionHomeFragmentToSearchFragment(
            category = category.name
        ).let { findNavController().navigate(it) }
    }

    private val productAdapter = productListAdapter()
    private val categoryAdapter = listAdapter(
        diffCallback { it.name },
        LayoutCategoryBinding::inflate
    ) { category: Category ->
        category.imageURL?.let { url ->
            loadImage(url, R.drawable.ic_product, imageView)
        }
        nameTextView.text = category.name
        root.setOnClickListener {
            onCategoryClick(category)
        }
    }
}