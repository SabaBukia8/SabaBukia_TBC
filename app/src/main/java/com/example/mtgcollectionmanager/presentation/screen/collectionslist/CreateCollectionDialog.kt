package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mtgcollectionmanager.R
import com.google.android.material.textfield.TextInputEditText

class CreateCollectionDialog(
    private val onCreateCollection: (name: String, description: String) -> Unit
) : DialogFragment() {

    private lateinit var tvDialogTitle: TextView
    private lateinit var etCollectionName: TextInputEditText
    private lateinit var etCollectionDescription: TextInputEditText
    private lateinit var btnCreate: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDialogTitle = view.findViewById(R.id.tvDialogTitle)
        etCollectionName = view.findViewById(R.id.etCollectionName)
        etCollectionDescription = view.findViewById(R.id.etCollectionDescription)
        btnCreate = view.findViewById(R.id.btnCreate)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnCreate.setOnClickListener {
            val name = etCollectionName.text?.toString()?.trim() ?: ""
            val description = etCollectionDescription.text?.toString()?.trim() ?: ""

            if (name.isEmpty()) {
                etCollectionName.error = "Collection name is required"
                return@setOnClickListener
            }

            onCreateCollection(name, description)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        // Show keyboard automatically
        etCollectionName.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
