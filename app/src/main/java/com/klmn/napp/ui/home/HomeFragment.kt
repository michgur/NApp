package com.klmn.napp.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutItemBinding
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
            viewModel.products.collect { products ->
                (recyclerView.adapter as Adapter).strings = products.map { it.product_name }
            }
        }

        Unit
    }

    class Adapter : RecyclerView.Adapter<ViewBoundHolder<LayoutItemBinding>>() {
        var strings: List<String> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewBoundHolder(parent, LayoutItemBinding::inflate)

        override fun onBindViewHolder(holder: ViewBoundHolder<LayoutItemBinding>, position: Int) {
            holder.binding.textView.text = strings[position]
        }

        override fun getItemCount() = strings.size
    }
}