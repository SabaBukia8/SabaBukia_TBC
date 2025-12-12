package com.example.mtgcollectionmanager.presentation.screen.collection.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.DialogColorFilterBinding
import com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract

class ColorFilterDialog(
    private val colorFilters: Map<String, CollectionContract.FilterState>,
    private val onFiltersApplied: (Map<String, CollectionContract.FilterState>) -> Unit
) : DialogFragment() {

    private var _binding: DialogColorFilterBinding? = null
    private val binding get() = _binding!!

    private val colors = listOf(
        "W" to "White",
        "U" to "Blue",
        "B" to "Black",
        "R" to "Red",
        "G" to "Green",
        "C" to "Colorless"
    )

    private val colorItems = mutableListOf<ColorFilterItem>()
    private lateinit var adapter: ColorFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MTGCollectionManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogColorFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }

    private fun setupRecyclerView() {
        // Initialize color items with current filter states
        colorItems.clear()
        colors.forEach { (code, name) ->
            val state = colorFilters[code] ?: CollectionContract.FilterState.NEUTRAL
            colorItems.add(ColorFilterItem(code, name, state))
        }

        adapter = ColorFilterAdapter(colorItems) { _, _ ->
            // State changes are handled directly in the adapter
        }

        binding.rvColors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ColorFilterDialog.adapter
        }
    }

    private fun setupListeners() {
        binding.btnApply.setOnClickListener {
            val filterMap = colorItems
                .filter { it.state != CollectionContract.FilterState.NEUTRAL }
                .associate { it.code to it.state }
            onFiltersApplied(filterMap)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnClearAll.setOnClickListener {
            colorItems.forEach { it.state = CollectionContract.FilterState.NEUTRAL }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
