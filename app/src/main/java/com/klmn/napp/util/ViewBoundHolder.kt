package com.klmn.napp.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class ViewBoundHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    constructor(
        parent: ViewGroup,
        inflate: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> T
    ) : this(inflate(LayoutInflater.from(parent.context), parent, false))
}