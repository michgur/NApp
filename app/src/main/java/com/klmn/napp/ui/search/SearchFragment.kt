package com.klmn.napp.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.FilterChip
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

    private val chips = mutableMapOf<String, FilterChip>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.doOnScroll(viewModel::onScroll)

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect(productAdapter::submitList)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filters.collect { filters ->
                for (filter in filters) filter.criterion.let {
                    chips[it] ?: createChip(it)
                }.filter = filter

                chips.values.filter { chip ->
                    chip.filter !in filters
                }.forEach(::removeChip)
            }
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Filter>("filter")
            ?.observe(viewLifecycleOwner) { filter ->
                viewModel.addFilter(filter)
            }

        viewModel.search(args.query, args.filters?.toList())

        binding.chipGroup.children.forEach { child ->
            (child as? FilterChip)?.let(::initChip)
        }

        binding.addChip.setOnClickListener {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToFilterDialogFragment()
            )
        }
    }

    private fun initChip(chip: FilterChip) = chip.apply {
        setOnClickListener {
            SearchFragmentDirections.actionSearchFragmentToFilterDialogFragment(
                filter ?: criterion?.let { Filter(it, "") }
            ).let(findNavController()::navigate)
        }
        setOnCloseIconClickListener {
            filter?.let(viewModel::removeFilter)
        }
        (filter?.criterion ?: criterion)?.let { chips[it] = this }
    }

    private fun createChip(criterion: String) = FilterChip(
        requireContext(),
        null,
        R.attr.filterChipStyle
    ).also { chip ->
        binding.chipGroup.addView(chip)
        chip.criterion = criterion
        initChip(chip)
    }

    // TODO: create a custom chipGroup class / adapter class
    private fun removeChip(chip: FilterChip) {
        if (chip.criterion != null) chip.clearFilter()
        else {
            chips.remove(chip.filter?.criterion)
            binding.chipGroup.removeView(chip)
        }
    }
}