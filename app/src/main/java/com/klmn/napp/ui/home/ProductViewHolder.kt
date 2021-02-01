package com.klmn.napp.ui.home

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.klmn.napp.R
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.ViewBoundHolder
import com.klmn.napp.util.formatQuantity
import com.klmn.napp.util.loadImage
import kotlinx.coroutines.flow.collect

class ProductViewHolder(
    private val host: Fragment,
    parent: ViewGroup
) : ViewBoundHolder<LayoutProductBinding>(parent, LayoutProductBinding::inflate) {
    fun bind(product: Product) = binding.run {
        nameTextView.text = product.name
        quantityTextView.text = host.requireContext().getString(
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

        loadImage(
            host.requireContext(),
            product.image_url,
            R.drawable.ic_product
        ).let {
            host.lifecycleScope.launchWhenStarted {
                it.collect {
                    imageView.setImageDrawable(it)
                    washImageView.setImageDrawable(it)
                }
            }
        }

        Unit
    }
}