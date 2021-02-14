package com.klmn.napp.ui.details

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentDetailsBinding
import com.klmn.napp.databinding.LayoutNutrientBinding
import com.klmn.napp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

@AndroidEntryPoint
class DetailsFragment : ViewBoundFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate),
    AppBarLayout.OnOffsetChangedListener {
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        viewModel.productId = args.productId

        lifecycleScope.launchWhenStarted {
            viewModel.product.collect { product ->
                product.imageURL?.let {
                    loadImage(product.imageURL, R.drawable.ic_product, imageView, washImageView)
                }
                collapsingToolbarLayout.title = product.name
                quantityTextView.text = getString(
                    R.string.quantity_with_unit,
                    formatQuantity(product.quantity, false),
                    product.unit
                )
            }
        }

        appbarLayout.addOnOffsetChangedListener(this@DetailsFragment)
        collapsedTint = requireContext().theme.resolveColorAttribute(R.attr.colorOnPrimary)
        expandedTint = requireContext().theme.resolveColorAttribute(R.attr.colorOnBackground)

        toolbar.setupWithNavController(findNavController())
        toolbar.setNavigationIcon(R.drawable.ic_back)

        nutrientsCard.run {
            if (collapsingContainerView.isExpanded) dropDownView.rotation = 180f
            nutrientsRecyclerView.adapter = SimpleAdapter
            headerView.setOnClickListener {
                dropDownView.animate()
                    .setDuration(200L)
                    .rotationBy(180f)
                    .start()
                collapsingContainerView.toggleExpanded()
            }
        }
    }

    object SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
        class ViewHolder(parent: ViewGroup) : ViewBoundHolder<LayoutNutrientBinding>(parent, LayoutNutrientBinding::inflate)

        val items = listOf(
            "protein",
            "fat",
            "carbohydrates"
        )
        val quantities = listOf("100g", "100g", "100g")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.binding.run {
            nameTextView.text = items[position]
            quantityTextView.text = quantities[position]
        }

        override fun getItemCount() = items.size
    }

    @ColorInt private var collapsedTint: Int = 0
    @ColorInt private var expandedTint: Int = 0

    override fun onOffsetChanged(appbar: AppBarLayout?, offset: Int) = binding.run {
        val fraction = abs(offset).toFloat() / appbarLayout.totalScrollRange
        toolbar.navigationIcon?.setTint(colorInterpolation(fraction, expandedTint, collapsedTint))
        toolbar.menu.findItem(R.id.action_add).icon.alpha = ((fraction - .5f) * 2 * 255).toInt()
    }
}