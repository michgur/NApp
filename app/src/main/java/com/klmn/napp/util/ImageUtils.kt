package com.klmn.napp.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

/*
* asynchronously load image into a stateFlow,
* will first emit the default drawable until image loads
* */
fun loadImage(
    context: Context,
    url: String,
    @DrawableRes defaultDrawable: Int
): StateFlow<Drawable> =
    ImageLoader(ContextCompat.getDrawable(context, defaultDrawable)!!).let { loader ->
        Glide.with(context)
            .asDrawable()
            .load(url)
            .into(loader)
        loader.flow
    }

/*
* a custom Glide target which contains a stateFlow with a default value that emits the image when loaded
* */
private class ImageLoader(defaultDrawable: Drawable) : CustomTarget<Drawable>() {
    private val _flow = MutableStateFlow(defaultDrawable)
    val flow get() = _flow.asStateFlow()

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        _flow.value = resource
    }
    override fun onLoadCleared(placeholder: Drawable?) = Unit
}

/*
* asynchronously load image into one or more imageViews
* display default drawable until image loads / on failure
* */
fun Fragment.loadImage(
    url: String,
    @DrawableRes defaultDrawable: Int,
    imageView: ImageView,
    vararg imageViews: ImageView) = lifecycleScope.launchWhenStarted {
    loadImage(requireContext(), url, defaultDrawable).collect { drawable ->
        imageView.setImageDrawable(drawable)
        imageViews.forEach { it.setImageDrawable(drawable) }
    }
}
