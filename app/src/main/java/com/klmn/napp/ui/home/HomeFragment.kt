package com.klmn.napp.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutProductBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.ViewBoundHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : ViewBoundFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        recyclerView.apply {
            adapter = Adapter()
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.products.collect {
                (recyclerView.adapter as Adapter).products = it
            }
        }

        Unit
    }

    class Adapter : RecyclerView.Adapter<ViewBoundHolder<LayoutProductBinding>>() {
        var products: List<Product> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewBoundHolder(parent, LayoutProductBinding::inflate)

        override fun onBindViewHolder(holder: ViewBoundHolder<LayoutProductBinding>, position: Int) {
            val product = products[position]
            holder.binding.apply {
                nameTextView.text = product.product_name
                quantityTextView.text = product.quantity
                carbTextView.text = product.nutriments.carbohydrates_100g
                fatTextView.text = product.nutriments.fat_100g
                proteinTextView.text = product.nutriments.proteins_100g
                energyUnitTextView.text = product.nutriments.energy_unit
                energyTextView.text = product.nutriments.energy

                root.setOnClickListener {
                    root.dispatchSetSelected(true)
                }

                Glide.with(root)
                    .load(product.image_small_url)
                    .fitCenter()
                    .into(imageView)
                Glide.with(root)
                    .load(product.image_small_url)
                    .centerCrop()
                    .into(washImageView)
            }
        }

        override fun getItemCount() = products.size
    }
}