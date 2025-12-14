package com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.BottomSheetManageCategoriesBinding
import com.example.mtgcollectionmanager.domain.model.Category
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManageCategoriesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageCategoriesBinding? = null
    private val binding get() = _binding!!

    private var categories: List<Category> = emptyList()
    private var onCreateCategory: ((name: String, color: String) -> Unit)? = null
    private var onEditCategory: ((id: Long, name: String, color: String) -> Unit)? = null
    private var onDeleteCategory: ((id: Long) -> Unit)? = null

    private val adapter by lazy {
        CategoryManagementAdapter(
            onEditClick = { category ->
                showEditCategoryDialog(category)
            },
            onDeleteClick = { category ->
                showDeleteConfirmationDialog(category)
            }
        )
    }

    private val predefinedColors = listOf(
        "#FF0000", // Red
        "#00FF00", // Green
        "#0000FF", // Blue
        "#FFFF00", // Yellow
        "#FF00FF", // Magenta
        "#00FFFF", // Cyan
        "#FFA500", // Orange
        "#800080", // Purple
        "#008000", // Dark Green
        "#000080", // Navy
        "#808080", // Gray
        "#FFC0CB"  // Pink
    )

    fun setCategories(categories: List<Category>) {
        this.categories = categories
    }

    fun setOnCreateCategoryListener(listener: (name: String, color: String) -> Unit) {
        this.onCreateCategory = listener
    }

    fun setOnEditCategoryListener(listener: (id: Long, name: String, color: String) -> Unit) {
        this.onEditCategory = listener
    }

    fun setOnDeleteCategoryListener(listener: (id: Long) -> Unit) {
        this.onDeleteCategory = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetManageCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ManageCategoriesBottomSheet.adapter
        }
        adapter.submitList(categories)
    }

    private fun setupListeners() {
        binding.btnCreateCategory.setOnClickListener {
            showCreateCategoryDialog()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun showCreateCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_category, null)

        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val rvColorPicker = dialogView.findViewById<RecyclerView>(R.id.rvColorPicker)

        var selectedColor = predefinedColors[0]

        // Setup color picker
        rvColorPicker.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = ColorPickerAdapter(predefinedColors) { color ->
                selectedColor = color
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_category)
            .setView(dialogView)
            .setPositiveButton(R.string.create) { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    onCreateCategory?.invoke(name, selectedColor)
                    dismiss()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_category, null)

        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val rvColorPicker = dialogView.findViewById<RecyclerView>(R.id.rvColorPicker)

        etName.setText(category.name)
        var selectedColor = category.color

        // Setup color picker
        rvColorPicker.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = ColorPickerAdapter(predefinedColors, category.color) { color ->
                selectedColor = color
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_category)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    onEditCategory?.invoke(category.id, name, selectedColor)
                    dismiss()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        val message = if (category.cardCount > 0) {
            getString(
                R.string.delete_category_with_cards_message,
                category.name,
                category.cardCount
            )
        } else {
            getString(R.string.delete_category_message, category.name)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_category_confirm)
            .setMessage(message)
            .setPositiveButton(R.string.delete) { _, _ ->
                onDeleteCategory?.invoke(category.id)
                dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Simple color picker adapter
    private class ColorPickerAdapter(
        private val colors: List<String>,
        private var selectedColor: String? = null,
        private val onColorClick: (String) -> Unit
    ) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ColorViewHolder(view)
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            val color = colors[position]
            holder.itemView.apply {
                try {
                    setBackgroundColor(Color.parseColor(color))
                } catch (e: Exception) {
                    setBackgroundColor(Color.GRAY)
                }

                // Add border if selected
                if (color == selectedColor) {
                    setPadding(4, 4, 4, 4)
                } else {
                    setPadding(0, 0, 0, 0)
                }

                layoutParams = ViewGroup.LayoutParams(120, 120)

                setOnClickListener {
                    selectedColor = color
                    onColorClick(color)
                    notifyDataSetChanged()
                }
            }
        }

        override fun getItemCount() = colors.size

        class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    companion object {
        fun newInstance(
            categories: List<Category>,
            onCreateCategory: (name: String, color: String) -> Unit,
            onEditCategory: (id: Long, name: String, color: String) -> Unit,
            onDeleteCategory: (id: Long) -> Unit
        ): ManageCategoriesBottomSheet {
            return ManageCategoriesBottomSheet().apply {
                setCategories(categories)
                setOnCreateCategoryListener(onCreateCategory)
                setOnEditCategoryListener(onEditCategory)
                setOnDeleteCategoryListener(onDeleteCategory)
            }
        }
    }
}
