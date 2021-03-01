
package com.klmn.napp.ui.search

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.klmn.napp.R
import com.klmn.napp.databinding.DialogFilterBinding
import com.klmn.napp.model.Filter
import com.klmn.napp.util.ViewBoundDialogFragment
import com.klmn.napp.util.hideKeyboard

class FilterDialogFragment : ViewBoundDialogFragment<DialogFilterBinding>(DialogFilterBinding::inflate) {
    private lateinit var criteria: Array<String>

    private val args: FilterDialogFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.criteria_filters,
            android.R.layout.simple_spinner_item
        ).let { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            criteriaSpinner.adapter = adapter
        }

        criteria = resources.getStringArray(R.array.criteria_ids)
        args.filter?.criterion?.let { criteria.indexOf(it) }?.takeIf { it >= 0 }?.let {
            criteriaSpinner.setSelection(it)
        }

        containsSwitch.apply {
            setOnCheckedChangeListener { _, isChecked ->
                updateContainsText(isChecked)
            }
            isChecked = args.filter?.contains ?: true
        }

        filterText.setText(args.filter?.value)

        cancelButton.setOnClickListener {
            dismiss()
        }
        doneButton.setOnClickListener {
            onActionDone()
        }
    }

    private fun onActionDone() = Filter(
        criteria[binding.criteriaSpinner.selectedItemPosition],
        binding.filterText.text.toString(),
        binding.containsSwitch.isChecked
    ).let { filter ->
        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.set("filter", filter)
        dismiss()
    }

    private fun updateContainsText(contains: Boolean) = binding.containsText.setText(
        if (contains) R.string.filter_contains
        else R.string.filter_doesnt_contain
    )

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }
}