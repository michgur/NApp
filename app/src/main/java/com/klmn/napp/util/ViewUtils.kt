package com.klmn.napp.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

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
