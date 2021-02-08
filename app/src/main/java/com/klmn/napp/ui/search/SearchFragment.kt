package com.klmn.napp.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.FilterChip
import com.klmn.napp.ui.components.FilterChipGroupAdapter
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
    private val filterAdapter = FilterChipGroupAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.doOnScroll(viewModel::onScroll)

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect(productAdapter::submitList)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filters.collect(filterAdapter::submitFilters)
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Filter>("filter")
            ?.observe(viewLifecycleOwner) { filter ->
                viewModel.addFilter(filter)
            }

        viewModel.search(args.query, args.filters?.toList())

        filterAdapter.apply {
            setOnFilterChipClickListener {
                (it as? FilterChip)?.let(::editFilter)
            }
            setOnFilterChipCloseIconClickListener {
                (it as? FilterChip)?.let(::removeFilter)
            }

            attach(binding.chipGroup)
        }

        binding.addChip.setOnClickListener {
            editFilter()
        }
    }

    // open filter dialog with an optional preexisting filter to edit
    private fun editFilter(chip: FilterChip? = null) =
        SearchFragmentDirections.actionSearchFragmentToFilterDialogFragment(
            chip?.filter ?: chip?.criterion?.let { Filter(it, "") }
        ).let(findNavController()::navigate)

    private fun removeFilter(chip: FilterChip) = chip.filter?.let(viewModel::removeFilter)
}