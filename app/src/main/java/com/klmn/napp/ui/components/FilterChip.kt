package com.klmn.napp.ui.components

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.os.bundleOf
import com.google.android.material.chip.Chip
import com.klmn.napp.R
import com.klmn.napp.model.Filter

class FilterChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.filterChipStyle
) : Chip(context, attrs, defStyleAttr) {
    /* the chip's filter, when not null chip is checked & chip's text is the filter's value */
    var filter: Filter? = null
        set(value) {
            field = value
            text = value?.value ?: label
            isChecked = value != null
        }

    /* set a default criterion w/out checking the chip */
    var criterion: String? = null
        get() = filter?.criterion ?: field

    /* set a default display label for chips w/out an existing filter */
    var label: String? = null
        set(value) {
            field = value
            if (filter == null) text = value
        }

    /* clear the filter & set the chip as not checked */
    fun clearFilter() { filter = null }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        isCloseIconVisible = checked
    }

    /* avoid unwanted checking behavior */
    override fun toggle() = Unit

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FilterChip,
            defStyleAttr,
            0
        ).apply {
            try {
                label = getString(R.styleable.FilterChip_label)
                criterion = getString(R.styleable.FilterChip_criterion)
            } finally {
                recycle()
            }
        }
    }

    companion object {
        private const val KEY_FILTER = "filterChip.filter"
        private const val KEY_LABEL = "filterChip.label"
        private const val KEY_CRITERION = "filterChip.criterion"
        private const val KEY_SUPER = "filterChip.super"
    }

    override fun onSaveInstanceState() = bundleOf(
        KEY_FILTER to filter,
        KEY_LABEL to label,
        KEY_CRITERION to criterion,
        KEY_SUPER to super.onSaveInstanceState()
    )
    override fun onRestoreInstanceState(state: Parcelable?) = super.onRestoreInstanceState(
        if (state is Bundle) {
            filter = state.getParcelable(KEY_FILTER)
            label = state.getString(KEY_LABEL)
            criterion = state.getString(KEY_CRITERION)
            state.getParcelable(KEY_SUPER)
        }
        else state
    )
}