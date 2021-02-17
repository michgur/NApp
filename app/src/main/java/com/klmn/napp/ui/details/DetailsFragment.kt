package com.klmn.napp.ui.details

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.google.android.material.appbar.AppBarLayout
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentDetailsBinding
import com.klmn.napp.databinding.LayoutNutrientBinding
import com.klmn.napp.ui.components.FilterChip
import com.klmn.napp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class DetailsFragment : ViewBoundFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate),
    AppBarLayout.OnOffsetChangedListener {
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        viewModel.productId = args.productId

        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)

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
                nutrientsAdapter.submitList(product.nutrients.entries.toList())
                for ((label, values) in product.labels)
                    createLabelChipGroup(linearLayout, label, values) { chip ->
                        (chip as? FilterChip)?.let { onChipClick(chip) }
                    }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.favorite.collect(::setFavorite)
        }

        appbarLayout.addOnOffsetChangedListener(this@DetailsFragment)
        collapsedTint = requireContext().theme.resolveColorAttribute(R.attr.colorOnPrimary)
        expandedTint = requireContext().theme.resolveColorAttribute(R.attr.colorOnBackground)

        toolbar.setupWithNavController(findNavController())
        toolbar.setNavigationIcon(R.drawable.ic_back)
        addFab.setOnClickListener {
            viewModel.toggleFavorite()
        }

        nutrientsCard.run {
            dropDownView.doOnLayout {
                // only updates onRestoreInstanceState
                if (collapsingContainerView.isExpanded) it.rotation = 180f
            }
            nutrientsRecyclerView.adapter = nutrientsAdapter
            headerView.setOnClickListener {
                collapsingContainerView.toggleExpanded()
                dropDownView.animate()
                    .setDuration(200L)
                    .rotation(
                        if (collapsingContainerView.isExpanded) 180f
                        else 0f
                    ).start()
            }
        }
    }

    private val nutrientsAdapter = listAdapter(
        diffCallback { it.key },
        LayoutNutrientBinding::inflate
    ) { nutrient: Map.Entry<String, Float> ->
        val energy = nutrient.key == "Energy"
        nameTextView.text = nutrient.key
        quantityTextView.text = requireContext().getString(
            R.string.quantity_with_unit,
            formatQuantity(nutrient.value, !energy),
            if (energy) "kcal" else "g"
        )
    }

    private fun onChipClick(chip: FilterChip) = chip.filter?.let { filter ->
        findNavController().navigate(
            DetailsFragmentDirections.actionDetailsFragmentToSearchFragment(
                filters = arrayOf(filter)
            )
        )
    }

    private fun setFavorite(favorite: Boolean) {
        val starResource =
            if (favorite) R.drawable.ic_star_filled
            else R.drawable.ic_star_empty
        binding.addFab.apply {
            setImageResource(starResource)
            // floating action button disappearing icon bug
            if (isOrWillBeShown) {
                hide()
                show()
            }
        }
        binding.toolbar.menu.findItem(R.id.action_star).apply {
            setIcon(starResource)
            setOnMenuItemClickListener {
                viewModel.toggleFavorite()
                true
            }
        }
    }

    @ColorInt private var collapsedTint: Int = 0
    @ColorInt private var expandedTint: Int = 0

    override fun onOffsetChanged(appbar: AppBarLayout?, offset: Int) = binding.run {
        val fraction = abs(offset).toFloat() / appbarLayout.totalScrollRange
        toolbar.navigationIcon?.setTint(colorInterpolation(fraction, expandedTint, collapsedTint))
        toolbar.menu.findItem(R.id.action_star).icon.alpha = ((fraction - .5f) * 2 * 255).toInt()
    }
}