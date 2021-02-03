package com.klmn.napp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentSearchBinding
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : ViewBoundFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.doOnScroll(viewModel::onScroll)

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect(productAdapter::submitList)
        }

        viewModel.search(args.query)
    }

    private fun onSearch(query: String) = viewModel.search(query)

    private val productAdapter = listAdapter(
        diffCallback { it.name },
        LayoutProductBinding::inflate
    ) { product: Product ->
        nameTextView.text = product.name
        quantityTextView.text = requireContext().getString(
            R.string.quantity_with_unit,
            product.quantity,
            product.unit
        )
        carbTextView.text = formatQuantity(product.nutriments.carbohydrates_100g)
        fatTextView.text = formatQuantity(product.nutriments.fat_100g)
        proteinTextView.text = formatQuantity(product.nutriments.proteins_100g)
        energyUnitTextView.text = product.nutriments.energy_unit
        energyTextView.text = product.nutriments.energy.toString()
        veganImageView.isVisible = product.vegan

        root.setOnClickListener {
            root.dispatchSetSelected(true)
        }

        loadImage(product.image_url, R.drawable.ic_product, imageView, washImageView)
    }
}