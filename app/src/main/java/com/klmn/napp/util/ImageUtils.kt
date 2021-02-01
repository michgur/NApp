package com.klmn.napp.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

fun loadImage(
    context: Context,
    url: String,
    @DrawableRes defaultDrawable: Int
): StateFlow<Drawable> {
    val drawable = MutableStateFlow(ContextCompat.getDrawable(context, defaultDrawable)!!)
    Glide.with(context)
        .asDrawable()
        .load(url)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                drawable.value = resource
            }
            override fun onLoadCleared(placeholder: Drawable?) = Unit
        })
    return drawable.asStateFlow()
}