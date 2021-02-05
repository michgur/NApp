package com.klmn.napp.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.ui.components.productListAdapter
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.doOnScroll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : ViewBoundFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()

    private val productAdapter = productListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.doOnScroll(viewModel::onScroll)

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect(productAdapter::submitList)
        }

        viewModel.search(args.query)
    }

    private fun onSearch(query: String) = viewModel.search(query)
}