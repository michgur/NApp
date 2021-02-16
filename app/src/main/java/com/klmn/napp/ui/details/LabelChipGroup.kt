package com.klmn.napp.ui.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.klmn.napp.R
import com.klmn.napp.databinding.LayoutLabelBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.ui.components.FilterChip

private var criteria: Map<String, String>? = null
private fun getCriteria(context: Context) = criteria
    ?: context.resources.getStringArray(R.array.criteria_ids).let { ids ->
        context.resources.getStringArray(R.array.criteria_labels)
            .withIndex()
            .associate { (i, name) ->
                ids[i] to name
            }.also {
                criteria = it
            }
    }

fun createLabelChipGroup(
    parent: ViewGroup,
    label: String,
    values: List<String>,
    onChipClick: View.OnClickListener
) {
    val context = parent.context
    val labelText = getCriteria(context)[label]
    if (values.isEmpty() || labelText.isNullOrBlank()) return

    LayoutLabelBinding.inflate(LayoutInflater.from(context), parent, true).apply {
        nameTextView.text = labelText
        values.forEach { value ->
            FilterChip(context).apply {
                filter = Filter(label, value.replaceFirst(".*:".toRegex(), ""))
                isCloseIconVisible = false
                setEnsureMinTouchTargetSize(true)
                setOnClickListener(onChipClick)
                root.addView(this)
            }
        }
    }
}