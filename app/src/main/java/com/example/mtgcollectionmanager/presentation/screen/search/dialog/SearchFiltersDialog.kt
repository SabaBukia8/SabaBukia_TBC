package com.example.mtgcollectionmanager.presentation.screen.search.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.databinding.DialogSearchFiltersBinding
import com.example.mtgcollectionmanager.presentation.screen.search.model.ColorFilterState
import com.example.mtgcollectionmanager.presentation.screen.search.model.SearchFilters
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFiltersDialog(
    private val currentFilters: SearchFilters,
    private val onFiltersApplied: (SearchFilters) -> Unit
) : DialogFragment() {

    private var _binding: DialogSearchFiltersBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiService: ScryfallApiService

    private val colorFilters = currentFilters.colors.toMutableMap()
    private val selectedCardTypes = currentFilters.cardTypes.toMutableSet()
    private val selectedRarities = currentFilters.rarities.toMutableSet()
    private val selectedSets = currentFilters.sets.toMutableSet()

    private lateinit var colorAdapter: SearchColorFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MTGCollectionManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSearchFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorFilter()
        setupCardTypeFilters()
        setupRarityFilters()
        setupSetsFilter()
        setupRangeFilters()
        setupTextFilters()
        setupListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.9).toInt()
        )
        return dialog
    }

    private fun setupColorFilter() {
        val colors = listOf(
            "W" to "White",
            "U" to "Blue",
            "B" to "Black",
            "R" to "Red",
            "G" to "Green",
            "C" to "Colorless"
        )

        val colorItems = colors.map { (code, name) ->
            ColorFilterItem(
                code = code,
                name = name,
                state = colorFilters[code] ?: ColorFilterState.NEUTRAL
            )
        }

        colorAdapter = SearchColorFilterAdapter { code, state ->
            if (state == ColorFilterState.NEUTRAL) {
                colorFilters.remove(code)
            } else {
                colorFilters[code] = state
            }
        }

        with(binding) {
            rvColors.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = colorAdapter
            }
        }

        // Submit the initial list
        colorAdapter.submitList(colorItems)
    }

    private fun setupCardTypeFilters() = with(binding) {
        val typeChips = mapOf(
            chipCreature to "creature",
            chipInstant to "instant",
            chipSorcery to "sorcery",
            chipEnchantment to "enchantment",
            chipArtifact to "artifact",
            chipLand to "land",
            chipPlaneswalker to "planeswalker",
            chipBattle to "battle"
        )

        typeChips.forEach { (chip, type) ->
            chip.isChecked = selectedCardTypes.contains(type)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCardTypes.add(type)
                } else {
                    selectedCardTypes.remove(type)
                }
            }
        }
    }

    private fun setupRarityFilters() = with(binding) {
        val rarityChips = mapOf(
            chipCommon to "common",
            chipUncommon to "uncommon",
            chipRare to "rare",
            chipMythic to "mythic"
        )

        rarityChips.forEach { (chip, rarity) ->
            chip.isChecked = selectedRarities.contains(rarity)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedRarities.add(rarity)
                } else {
                    selectedRarities.remove(rarity)
                }
            }
        }
    }

    private fun setupSetsFilter() = with(binding) {
        updateSelectedSetsChips()

        btnSelectSets.setOnClickListener {
            val setDialog = SetSelectionDialog(selectedSets) { sets ->
                selectedSets.clear()
                selectedSets.addAll(sets)
                updateSelectedSetsChips()
            }
            setDialog.show(parentFragmentManager, "SetSelectionDialog")
        }
    }

    private fun updateSelectedSetsChips() = with(binding) {
        cgSelectedSets.removeAllViews()

        if (selectedSets.isEmpty()) {
            addNoSetsSelectedChip()
        } else {
            selectedSets.forEach { setCode ->
                addSetChip(setCode)
            }
        }
    }

    private fun addNoSetsSelectedChip() {
        val chip = Chip(requireContext()).apply {
            text = "No sets selected"
            isEnabled = false
        }
        binding.cgSelectedSets.addView(chip)
    }

    private fun addSetChip(setCode: String) {
        val chip = Chip(requireContext()).apply {
            text = setCode.uppercase()
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedSets.remove(setCode)
                updateSelectedSetsChips()
            }
        }
        binding.cgSelectedSets.addView(chip)
    }

    private fun setupRangeFilters() = with(binding) {
        currentFilters.manaValueMin?.let {
            etManaValueMin.setText(it.toString())
        }
        currentFilters.manaValueMax?.let {
            etManaValueMax.setText(it.toString())
        }

        currentFilters.priceMin?.let {
            etPriceMin.setText(it.toString())
        }
        currentFilters.priceMax?.let {
            etPriceMax.setText(it.toString())
        }

        currentFilters.yearMin?.let {
            etYearMin.setText(it.toString())
        }
        currentFilters.yearMax?.let {
            etYearMax.setText(it.toString())
        }
    }

    private fun setupTextFilters() = with(binding) {
        etArtist.setText(currentFilters.artist)
        etOracleText.setText(currentFilters.oracleText)
        etFlavorText.setText(currentFilters.flavorText)
    }

    private fun setupListeners() = with(binding) {
        btnApply.setOnClickListener {
            // Get the most current state from the adapter
            val updatedColorFilters = colorAdapter.getColorStates()

            val filters = SearchFilters(
                colors = updatedColorFilters,
                sets = selectedSets.toSet(),
                cardTypes = selectedCardTypes.toSet(),
                rarities = selectedRarities.toSet(),
                manaValueMin = etManaValueMin.text.toString().toIntOrNull(),
                manaValueMax = etManaValueMax.text.toString().toIntOrNull(),
                priceMin = etPriceMin.text.toString().toFloatOrNull(),
                priceMax = etPriceMax.text.toString().toFloatOrNull(),
                artist = etArtist.text.toString().trim(),
                yearMin = etYearMin.text.toString().toIntOrNull(),
                yearMax = etYearMax.text.toString().toIntOrNull(),
                oracleText = etOracleText.text.toString().trim(),
                flavorText = etFlavorText.text.toString().trim()
            )
            onFiltersApplied(filters)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnClearAll.setOnClickListener {
            clearAllFilters()
        }
    }

    private fun clearAllFilters() = with(binding) {
        // Clear colors using the adapter's method
        colorFilters.clear()
        colorAdapter.resetAllStates()

        // Clear card types
        selectedCardTypes.clear()
        cgCardTypes.children.filterIsInstance<Chip>().forEach {
            it.isChecked = false
        }

        // Clear rarities
        selectedRarities.clear()
        cgRarity.children.filterIsInstance<Chip>().forEach {
            it.isChecked = false
        }

        // Clear sets
        selectedSets.clear()
        updateSelectedSetsChips()

        // Clear ranges
        etManaValueMin.text?.clear()
        etManaValueMax.text?.clear()
        etPriceMin.text?.clear()
        etPriceMax.text?.clear()
        etYearMin.text?.clear()
        etYearMax.text?.clear()

        // Clear text fields
        etArtist.text?.clear()
        etOracleText.text?.clear()
        etFlavorText.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}