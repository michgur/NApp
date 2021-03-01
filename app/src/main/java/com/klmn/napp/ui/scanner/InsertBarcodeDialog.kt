package com.klmn.napp.ui.scanner

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.klmn.napp.databinding.DialogInsertBarcodeBinding
import com.klmn.napp.util.ViewBoundDialogFragment
import com.klmn.napp.util.hideKeyboard

class InsertBarcodeDialog : ViewBoundDialogFragment<DialogInsertBarcodeBinding>(DialogInsertBarcodeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        barcodeText.doAfterTextChanged { text ->
            doneButton.isEnabled = !text.isNullOrBlank()
        }
        barcodeText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) onActionDone()
            true
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
        doneButton.setOnClickListener {
            onActionDone()
        }
    }

    private fun onActionDone() {
        val barcode = binding.barcodeText.text.toString()
        if (barcode.isNotBlank()) {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("barcode", barcode)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }
}