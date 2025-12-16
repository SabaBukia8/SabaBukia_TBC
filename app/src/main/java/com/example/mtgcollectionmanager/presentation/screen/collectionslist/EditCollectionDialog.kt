package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.DialogCreateCollectionBinding

class EditCollectionDialog(
    private val collectionId: Long,
    private val currentName: String,
    private val currentDescription: String,
    private val onUpdateCollection: (collectionId: Long, name: String, description: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogCreateCollectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvDialogTitle.setText(R.string.edit_collection)
            btnCreate.text = getString(R.string.update)

            etCollectionName.setText(currentName)
            etCollectionDescription.setText(currentDescription)

            btnCreate.setOnClickListener {
                val name = etCollectionName.text?.toString()?.trim() ?: ""
                val description = etCollectionDescription.text?.toString()?.trim() ?: ""

                if (name.isEmpty()) {
                    etCollectionName.error = getString(R.string.collection_name_is_required)
                    return@setOnClickListener
                }

                onUpdateCollection(collectionId, name, description)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            etCollectionName.requestFocus()
            etCollectionName.setSelection(etCollectionName.text?.length ?: 0)
        }

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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