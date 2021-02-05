package com.klmn.napp.ui.components

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
    carbTextView.text = formatQuantity(product.carbs)
    fatTextView.text = formatQuantity(product.fat)
    proteinTextView.text = formatQuantity(product.protein)
    energyUnitTextView.text = getString(R.string.energy_unit)
    energyTextView.text = product.energy.toString()
    veganImageView.isVisible = product.vegan

    root.setOnClickListener {
        root.dispatchSetSelected(true)
    }

    product.imageURL?.let {
        loadImage(product.imageURL, R.drawable.ic_product, imageView, washImageView)
    }
}