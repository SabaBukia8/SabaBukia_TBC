package com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.BottomSheetEditCardBinding
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.Category
import com.example.mtgcollectionmanager.presentation.common.loadImage
import com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditCardBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditCardBinding? = null
    private val binding get() = _binding!!

    private var card: CollectionCardUiModel? = null
    private var categories: List<Category> = emptyList()
    private var onSave: ((quantity: Int, condition: String, notes: String, categoryId: Long?) -> Unit)? =
        null

    private var selectedQuantity: Int = 1
    private var selectedCondition: CardCondition = CardCondition.NEAR_MINT
    private var selectedCategoryId: Long? = null

    fun setCard(card: CollectionCardUiModel) {
        this.card = card
    }

    fun setCategories(categories: List<Category>) {
        this.categories = categories
    }

    fun setOnSaveListener(listener: (quantity: Int, condition: String, notes: String, categoryId: Long?) -> Unit) {
        this.onSave = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupSpinners()
        setupListeners()
    }

    private fun setupViews() {
        val cardData = card ?: return
        with(binding) {
            tvCardName.text = cardData.name
            tvSetName.text = cardData.setInfo
            ivCardImage.loadImage(cardData.imageUrl)
            etNotes.setText(cardData.notes)
        }
    }

    private fun setupSpinners() {
        val cardData = card ?: return

        // Quantity spinner (1-20)
        val quantities = (1..20).toList()
        binding.spinnerQuantity.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                quantities
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            // Set current quantity
            val currentQuantityIndex = quantities.indexOf(cardData.quantity).coerceAtLeast(0)
            setSelection(currentQuantityIndex)
            selectedQuantity = cardData.quantity

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedQuantity = quantities[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Condition spinner
        val conditions = CardCondition.values()
        val conditionNames = conditions.map { condition ->
            when (condition) {
                CardCondition.NEAR_MINT -> getString(R.string.condition_near_mint)
                CardCondition.LIGHTLY_PLAYED -> getString(R.string.condition_lightly_played)
                CardCondition.MODERATELY_PLAYED -> getString(R.string.condition_moderately_played)
                CardCondition.HEAVILY_PLAYED -> getString(R.string.condition_heavily_played)
                CardCondition.DAMAGED -> getString(R.string.condition_damaged)
            }
        }

        binding.spinnerCondition.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                conditionNames
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            // Set current condition
            val currentCondition = try {
                CardCondition.valueOf(cardData.condition)
            } catch (e: Exception) {
                CardCondition.NEAR_MINT
            }
            val currentConditionIndex = conditions.indexOf(currentCondition)
            setSelection(currentConditionIndex)
            selectedCondition = currentCondition

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCondition = conditions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Category spinner
        val categoryItems = mutableListOf<Pair<String, Long?>>()
        categoryItems.add(Pair(getString(R.string.no_category), null))
        categories.forEach { category ->
            categoryItems.add(Pair(category.name, category.id))
        }

        val categoryNames = categoryItems.map { it.first }

        binding.spinnerCategory.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            // Set current category (default to no category since UI model doesn't track it)
            val currentCategoryIndex = 0 // Default to "No Category"
            setSelection(currentCategoryIndex)
            selectedCategoryId = null

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategoryId = categoryItems[position].second
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val notes = binding.etNotes.text.toString()
            onSave?.invoke(selectedQuantity, selectedCondition.name, notes, selectedCategoryId)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            card: CollectionCardUiModel,
            categories: List<Category>,
            onSave: (quantity: Int, condition: String, notes: String, categoryId: Long?) -> Unit
        ): EditCardBottomSheet {
            return EditCardBottomSheet().apply {
                setCard(card)
                setCategories(categories)
                setOnSaveListener(onSave)
            }
        }
    }
}
