package com.klmn.napp.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.animation.ArgbEvaluatorCompat

/*
* preform callback on scroll, receives the scroll position
* */
fun RecyclerView.doOnScroll(callback: (position: Int) -> Unit) =
    addOnScrollListener(ScrollListener(callback))

private class ScrollListener(
    private val callback: (Int) -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        recyclerView.layoutManager?.let { layoutManager ->
            when(layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                is StaggeredGridLayoutManager ->
                    layoutManager.findFirstVisibleItemPositions(null).last()
                else -> throw IllegalStateException("layout manager class ${layoutManager::class}" +
                    "is not supported by doOnScroll()")
            }.let(callback)
        }
    }
}

fun Activity.hideKeyboard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let { focus ->
        inputManager.hideSoftInputFromWindow(
            focus.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Fragment.hideKeyboard() = requireActivity().hideKeyboard()

@ColorInt
fun colorInterpolation(
    @FloatRange(from = 0.0, to = 1.0) fraction: Float,
    @ColorInt from: Int,
    @ColorInt to: Int
): Int = ArgbEvaluatorCompat.getInstance().evaluate(fraction, from, to)

@ColorInt
fun Resources.Theme.resolveColorAttribute(
    @AttrRes attrId: Int
): Int = TypedValue().let { value ->
    resolveAttribute(attrId, value, true)
    value.data
}

fun Fragment.makeToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), text, length).show()

fun Fragment.makeToast(
    @StringRes resId: Int,
    vararg formatArgs: Any?,
    length: Int = Toast.LENGTH_SHORT
) = Toast.makeText(requireContext(), getString(resId, *formatArgs), length).show()

/* rotate rect in multiples of 90 degrees, clockwise rotation */
fun Rect.rotate(rotation: Int) = when (rotation % 4) {
    0 -> Rect(this)
    1 -> Rect(top, right, bottom, left)
    2 -> Rect(right, bottom, left, top)
    3 -> Rect(bottom, left, top, right)
    else -> throw IllegalStateException()
}
