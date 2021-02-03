package com.klmn.napp.ui.home

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutCategoryBinding
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Category
import com.klmn.napp.model.Product
import com.klmn.napp.util.*
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

        initActionBar((requireActivity() as AppCompatActivity).supportActionBar)
        Unit
    }

    private fun onSearch(query: String) = viewModel.search(query)

    private val categoryAdapter = listAdapter(
        diffCallback { it.name },
        LayoutCategoryBinding::inflate
    ) { category: Category ->
        category.imageURL?.let { url ->
            loadImage(url, R.drawable.ic_product, imageView)
        }
        nameTextView.text = category.name
    }

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

    private fun initActionBar(actionBar: ActionBar?) = actionBar?.apply {
        setDisplayShowCustomEnabled(true)
        setDisplayShowTitleEnabled(false)
        setDisplayShowHomeEnabled(false)
        setCustomView(R.layout.layout_search)
        (customView as EditText).setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                onSearch(textView.text.toString())
            true
        }
    }
}