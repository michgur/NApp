package com.klmn.napp.ui

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.klmn.napp.R
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.diffCallback
import com.klmn.napp.util.formatQuantity
import com.klmn.napp.util.listAdapter
import com.klmn.napp.util.loadImage

fun Fragment.productListAdapter() = listAdapter(
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