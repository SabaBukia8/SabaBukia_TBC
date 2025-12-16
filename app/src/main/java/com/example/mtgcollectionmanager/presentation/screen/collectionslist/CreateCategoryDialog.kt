package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.mtgcollectionmanager.databinding.DialogCreateCategoryBinding

class CreateCategoryDialog(
    private val collectionId: Long,
    private val onCreateCategory: (collectionId: Long, name: String, color: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var colorPickerAdapter: ColorPickerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()

        with(binding) {
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

            etCategoryName.requestFocus()
        }

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun setupColorPicker() {
        colorPickerAdapter = ColorPickerAdapter { selectedColor -> }
        binding.rvColorPicker.adapter = colorPickerAdapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}