package com.klmn.napp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.FilterChip
import com.klmn.napp.ui.components.FilterChipGroupAdapter
import com.klmn.napp.ui.components.productListAdapter
import com.klmn.napp.ui.components.setup
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.doOnScroll
import com.klmn.napp.util.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : ViewBoundFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()

    private val productAdapter = productListAdapter()
    private val filterAdapter = FilterChipGroupAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        lifecycleScope.launchWhenStarted {
            viewModel.products.collect { products ->
                productAdapter.submitList(products.toList()) {
                    if (products.isNotEmpty()) progressBar.isVisible = false
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filters.collect(filterAdapter::submitFilters)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.errors.collect { e ->
                e.message?.let(::makeToast)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect {
                if (it) progressBar.isVisible = true // hidden only after products are submitted
                emptyText.isVisible = !it && viewModel.lastPage && viewModel.products.value.isEmpty()
            }
        }

        // receive filters from filterDialogFragments
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Filter>("filter")
            ?.observe(viewLifecycleOwner, viewModel::addFilter)

        viewModel.search(args.query, args.filters?.toList())

        toolbar.searchEditText.setText(args.query)
        toolbar.setup(onSearch = ::onSearch, onScan = ::onScan)

        productsRecyclerView.apply {
            adapter = productAdapter
            doOnScroll(viewModel::onScroll)
        }

        filterAdapter.apply {
            setOnFilterChipClickListener {
                (it as? FilterChip)?.let(::editFilter)
            }
            setOnFilterChipCloseIconClickListener {
                (it as? FilterChip)?.let(::removeFilter)
            }

            attach(chipGroup)
        }

        addChip.setOnClickListener {
            editFilter()
        }
    }

    // open filter dialog with an optional preexisting filter to edit
    private fun editFilter(chip: FilterChip? = null) =
        SearchFragmentDirections.actionSearchFragmentToFilterDialogFragment(
            chip?.filter ?: chip?.criterion?.let { Filter(it, "") }
        ).let(findNavController()::navigate)

    private fun removeFilter(chip: FilterChip) = chip.filter?.let(viewModel::removeFilter)

    private fun onScan() = findNavController().navigate(
        SearchFragmentDirections.actionSearchFragmentToScannerFragment()
    )

    private fun onSearch(query: String) {
        SearchFragmentDirections.actionSearchFragmentSelf(
            query,
            viewModel.filters.value.toTypedArray()
        ).let(findNavController()::navigate)

        binding.toolbar.searchEditText.setText(args.query)
    }
}