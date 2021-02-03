package com.klmn.napp.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

/*
* creates a diffUtil ItemCallback that uses parameter fun getId() to check if items
* are the same & the class' equals() to check if contents are the same
* */
@SuppressLint("DiffUtilEquals")
fun <T, ID> diffCallback(getId: (item: T) -> ID) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = getId(oldItem) == getId(newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

/*
* creates a ListAdapter with items of type T & viewHolders of type ViewBoundHolder<T>,
* that inflates bound views for the viewHolders with inflate(),
* and binds viewHolders to specific items with bind()
* */
fun <T, B : ViewBinding> listAdapter(
    diffCallback: DiffUtil.ItemCallback<T>,
    inflate: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> B,
    bind: B.(item: T) -> Unit
) = object : ListAdapter<T, ViewBoundHolder<B>>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewBoundHolder(parent, inflate)
    override fun onBindViewHolder(holder: ViewBoundHolder<B>, position: Int) =
        holder.binding.bind(currentList[position])
}