package com.klmn.napp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.klmn.napp.databinding.FragmentHomeBinding
import com.klmn.napp.databinding.LayoutItemBinding
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.ViewBoundHolder

class HomeFragment : ViewBoundFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        recyclerView.apply {
            adapter = Adapter()
            layoutManager = LinearLayoutManager(requireContext())
        }

        Unit
    }

    class Adapter : RecyclerView.Adapter<ViewBoundHolder<LayoutItemBinding>>() {
        var strings: List<String> = listOf("a", "b", "c")
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