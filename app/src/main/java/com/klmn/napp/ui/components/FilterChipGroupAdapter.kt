package com.klmn.napp.ui.components

import android.view.View
import androidx.core.view.children
import com.google.android.material.chip.ChipGroup
import com.klmn.napp.R
import com.klmn.napp.model.Filter

/* a class that manages a chipGroup of filterChips & can match it to filter lists */
class FilterChipGroupAdapter {
    private lateinit var chipGroup: ChipGroup
    private val chips = mutableMapOf<String, FilterChip>()

    private var onChipClickListener: View.OnClickListener? = null
    private var onChipCloseListener: View.OnClickListener? = null

    /* attach this adapter to a chipGroup. must be called BEFORE setting filters */
    fun attach(chipGroup: ChipGroup) {
        chips.clear()
        this.chipGroup = chipGroup
        chipGroup.children.forEach {
            (it as? FilterChip)?.let(::addChip)
        }
    }

    /*
    * set the filter chips to match a list of filters.
    * chips with labels will remain visible but their filter will be changed / removed.
    * the style attr is used to create new chips if necessary
    * */
    fun submitFilters(filters: List<Filter>, defChipStyleAttr: Int = R.attr.filterChipStyle) {
        // set existing chips / create new chips for filters
        filters.forEach { filter ->
            chips.getOrPut(filter.criterion) {
                createChip(defChipStyleAttr)
            }.filter = filter
        }

        // remove redundant filter chips
        chips.values.filter { it.filter !in filters }.forEach(::removeChip)
    }

    /* set an onClickListener for the filterChips in the group (& only them) */
    fun setOnFilterChipClickListener(listener: View.OnClickListener) {
        chips.values.forEach { it.setOnClickListener(listener) }
        onChipClickListener = listener
    }

    /* set a onCloseIconClickListener for the filterChips in the group (& only them) */
    fun setOnFilterChipCloseIconClickListener(listener: View.OnClickListener) {
        chips.values.forEach { it.setOnCloseIconClickListener(listener) }
        onChipCloseListener = listener
    }

    private fun createChip(defChipStyleAttr: Int) = FilterChip(
        chipGroup.context,
        null,
        defChipStyleAttr
    ).also(::addChip)

    private fun addChip(chip: FilterChip) {
        // remove previous chip if exists & add new chip to map
        (chip.criterion)?.let { criterion ->
            chips[criterion]?.let(chipGroup::removeView)
            chips[criterion] = chip
        }
        // add chip to group if it is not already a child
        if (chip !in chipGroup.children) chipGroup.addView(chip)
        // set click listeners
        chip.setOnClickListener(onChipClickListener)
        chip.setOnCloseIconClickListener(onChipCloseListener)
    }

    private fun removeChip(chip: FilterChip) {
        if (chip.label != null) chip.clearFilter()
        else {
            chipGroup.removeView(chip)
            chips.remove(chip.criterion)
        }
    }
}