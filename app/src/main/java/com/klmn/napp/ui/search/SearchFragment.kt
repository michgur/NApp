package com.klmn.napp.ui.search

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.klmn.napp.R
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.OpenFoodFactsAPI.Criteria.ALLERGENS
import com.klmn.napp.data.network.OpenFoodFactsAPI.Criteria.BRANDS
import com.klmn.napp.data.network.OpenFoodFactsAPI.Criteria.CATEGORIES
import com.klmn.napp.data.network.OpenFoodFactsAPI.Criteria.COUNTRIES
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.productListAdapter
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.doOnScroll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class SearchFragment : ViewBoundFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()

    private val productAdapter = productListAdapter()

    private val chips = mutableMapOf<String, Chip>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.doOnScroll(viewModel::onScroll)

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect(productAdapter::submitList)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filters.collect { filters ->
                for (filter in filters)
                    chips[filter.criterion]?.apply {
                        text = filter.value
                        setChipBackgroundColorResource(R.color.yellow_a400)
                        chipStrokeWidth = 1f
                        setChipStrokeColorResource(R.color.black)
                    }
            }
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Filter>("filter")
            ?.observe(viewLifecycleOwner) { filter ->
                viewModel.addFilter(filter)
            }

        viewModel.search(args.query, args.filters?.toList())

        for (crit in listOf(CATEGORIES, COUNTRIES, ALLERGENS, BRANDS)) {
            Chip(requireContext()).apply {
                text = crit.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                setTextColor(Color.BLACK)
                setEnsureMinTouchTargetSize(false)
                setChipBackgroundColorResource(R.color.yellow_a700)
                setOnClickListener {
                    findNavController().navigate(R.id.action_searchFragment_to_filterDialogFragment)
                }
            }.let {
                chips[crit] = it
                chipGroup.addView(it)
            }
        }
    }
}