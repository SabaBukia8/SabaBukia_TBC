package com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.BottomSheetManageCollectionsBinding
import com.example.mtgcollectionmanager.presentation.model.CollectionUi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManageCollectionsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageCollectionsBinding? = null
    private val binding get() = _binding!!

    private var collections: List<CollectionUi> = emptyList()
    private var onCreateCollection: ((name: String, description: String) -> Unit)? = null
    private var onEditCollection: ((id: Long, name: String, description: String) -> Unit)? = null
    private var onDeleteCollection: ((id: Long) -> Unit)? = null

    private val adapter by lazy {
        CollectionAdapter(
            onEditClick = { collection ->
                showEditCollectionDialog(collection)
            },
            onDeleteClick = { collection ->
                showDeleteConfirmationDialog(collection)
            }
        )
    }

    fun setCollections(collections: List<CollectionUi>) {
        this.collections = collections
    }

    fun setOnCreateCollectionListener(listener: (name: String, description: String) -> Unit) {
        this.onCreateCollection = listener
    }

    fun setOnEditCollectionListener(listener: (id: Long, name: String, description: String) -> Unit) {
        this.onEditCollection = listener
    }

    fun setOnDeleteCollectionListener(listener: (id: Long) -> Unit) {
        this.onDeleteCollection = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetManageCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvCollections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ManageCollectionsBottomSheet.adapter
        }
        adapter.submitList(collections)
    }

    private fun setupListeners() {
        binding.btnCreateCollection.setOnClickListener {
            showCreateCollectionDialog()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun showCreateCollectionDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_collection, null)

        val etName = dialogView.findViewById<EditText>(R.id.etCollectionName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etCollectionDescription)
        val btnCreate = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCreate)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            if (name.isNotEmpty()) {
                onCreateCollection?.invoke(name, description)
                dialog.dismiss()
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditCollectionDialog(collection: CollectionUi) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_collection, null)

        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val etName = dialogView.findViewById<EditText>(R.id.etCollectionName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etCollectionDescription)
        val btnCreate = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCreate)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        tvTitle.setText(R.string.edit_collection)
        btnCreate.setText(R.string.save)
        etName.setText(collection.name)
        etDescription.setText(collection.description)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            if (name.isNotEmpty()) {
                onEditCollection?.invoke(collection.id, name, description)
                dialog.dismiss()
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(collection: CollectionUi) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_collection_confirm)
            .setMessage(getString(R.string.delete_collection_message, collection.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                onDeleteCollection?.invoke(collection.id)
                dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            collections: List<CollectionUi>,
            onCreateCollection: (name: String, description: String) -> Unit,
            onEditCollection: (id: Long, name: String, description: String) -> Unit,
            onDeleteCollection: (id: Long) -> Unit
        ): ManageCollectionsBottomSheet {
            return ManageCollectionsBottomSheet().apply {
                setCollections(collections)
                setOnCreateCollectionListener(onCreateCollection)
                setOnEditCollectionListener(onEditCollection)
                setOnDeleteCollectionListener(onDeleteCollection)
            }
        }
    }
}
