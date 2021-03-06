package com.klmn.napp.ui.components

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.klmn.napp.R
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.diffCallback
import com.klmn.napp.util.formatQuantity
import com.klmn.napp.util.listAdapter
import com.klmn.napp.util.loadImage

fun Fragment.productListAdapter() = listAdapter(
    diffCallback { it.id },
    LayoutProductBinding::inflate
) { product: Product ->
    nameTextView.text = product.name
    quantityTextView.text = requireContext().getString(
        R.string.quantity_with_unit,
        formatQuantity(product.quantity, false),
        product.unit
    )
    carbTextView.text = formatQuantity(product.carbs)
    fatTextView.text = formatQuantity(product.fat)
    proteinTextView.text = formatQuantity(product.protein)
    energyUnitTextView.text = getString(R.string.energy_unit)
    energyTextView.text = product.energy.toString()
    veganImageView.isVisible = product.vegan
    starImageView.isVisible = product.favorite

    root.setOnClickListener {
        findNavController().navigate(
            R.id.detailsFragment,
            bundleOf("productId" to product.id),
            null,
            FragmentNavigatorExtras(
                imageView to "product_image",
                washImageView to "product_image_wash"
            )
        )
    }

    imageView.transitionName = "product_image_${product.imageURL}"
    washImageView.transitionName = "product_image_wash_${product.imageURL}"
    product.imageURL?.let {
        loadImage(product.imageURL, R.drawable.ic_product, imageView, washImageView)
    }
}
