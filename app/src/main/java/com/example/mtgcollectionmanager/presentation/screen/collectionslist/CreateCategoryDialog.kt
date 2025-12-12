package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.google.android.material.textfield.TextInputEditText

class CreateCategoryDialog(
    private val collectionId: Long,
    private val onCreateCategory: (collectionId: Long, name: String, color: String) -> Unit
) : DialogFragment() {

    private lateinit var tvDialogTitle: TextView
    private lateinit var etCategoryName: TextInputEditText
    private lateinit var rvColorPicker: RecyclerView
    private lateinit var btnCreate: Button
    private lateinit var btnCancel: Button
    private lateinit var colorPickerAdapter: ColorPickerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDialogTitle = view.findViewById(R.id.tvDialogTitle)
        etCategoryName = view.findViewById(R.id.etCategoryName)
        rvColorPicker = view.findViewById(R.id.rvColorPicker)
        btnCreate = view.findViewById(R.id.btnCreate)
        btnCancel = view.findViewById(R.id.btnCancel)

        setupColorPicker()

        btnCreate.setOnClickListener {
            val name = etCategoryName.text?.toString()?.trim() ?: ""

            if (name.isEmpty()) {
                etCategoryName.error = "Category name is required"
                return@setOnClickListener
            }

            val selectedColor = colorPickerAdapter.getSelectedColor()
            onCreateCategory(collectionId, name, selectedColor)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        // Show keyboard automatically
        etCategoryName.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun setupColorPicker() {
        colorPickerAdapter = ColorPickerAdapter { selectedColor ->
            // Color selected callback - nothing to do here as adapter handles selection
        }
        rvColorPicker.adapter = colorPickerAdapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
