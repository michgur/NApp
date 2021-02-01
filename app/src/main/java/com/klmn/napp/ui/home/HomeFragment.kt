package com.klmn.napp.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.model.Product
import com.klmn.napp.util.ViewBoundFragment
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

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
            setCustomView(R.layout.layout_search)
            (customView as EditText).setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    viewModel.query.value = textView.text.toString()
                true
            }
        }

        Unit
    }

    inner class Adapter : RecyclerView.Adapter<ProductViewHolder>() {
        var products: List<Product> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount() = products.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ProductViewHolder(this@HomeFragment, parent)

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
            holder.bind(products[position])
    }
}