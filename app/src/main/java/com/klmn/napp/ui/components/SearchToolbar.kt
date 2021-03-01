package com.klmn.napp.ui.components

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.inputmethod.EditorInfo
import com.klmn.napp.databinding.LayoutToolbarBinding

fun LayoutToolbarBinding.setup(
    onSearch: (query: String) -> Unit,
    onScan: () -> Unit
) {
    root.touchDelegate = searchEditText.let { searchBox ->
        val padding = scanButton.width + root.paddingEnd
        val rect = Rect().also {
            searchBox.getHitRect(it)
            it.left -= padding
            it.right -= padding
        }
        TouchDelegate(rect, searchBox)
    }
    searchEditText.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE && !searchEditText.text.isNullOrBlank())
            onSearch(searchEditText.text.toString())
        true
    }
    scanButton.setOnClickListener {
        onScan()
    }
}