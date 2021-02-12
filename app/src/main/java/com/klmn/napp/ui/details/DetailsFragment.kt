package com.klmn.napp.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentDetailsBinding
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailsFragment : ViewBoundFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {
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
            }
        }

        toolbar.setupWithNavController(findNavController())
    }
}