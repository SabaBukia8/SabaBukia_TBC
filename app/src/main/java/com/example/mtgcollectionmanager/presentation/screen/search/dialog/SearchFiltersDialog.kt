package com.example.mtgcollectionmanager.presentation.screen.search.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.model.remote.SetDto
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.databinding.DialogSearchFiltersBinding
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.screen.search.model.ColorFilterState
import com.example.mtgcollectionmanager.presentation.screen.search.model.SearchFilters
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        }.toMutableList()

        colorAdapter = SearchColorFilterAdapter(colorItems) { code, state ->
            if (state == ColorFilterState.NEUTRAL) {
                colorFilters.remove(code)
            } else {
                colorFilters[code] = state
            }
        }

        binding.rvColors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = colorAdapter
        }
    }

    private fun setupCardTypeFilters() {
        val typeChips = mapOf(
            binding.chipCreature to "creature",
            binding.chipInstant to "instant",
            binding.chipSorcery to "sorcery",
            binding.chipEnchantment to "enchantment",
            binding.chipArtifact to "artifact",
            binding.chipLand to "land",
            binding.chipPlaneswalker to "planeswalker",
            binding.chipBattle to "battle"
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

    private fun setupRarityFilters() {
        val rarityChips = mapOf(
            binding.chipCommon to "common",
            binding.chipUncommon to "uncommon",
            binding.chipRare to "rare",
            binding.chipMythic to "mythic"
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

    private fun setupSetsFilter() {
        updateSelectedSetsChips()

        binding.btnSelectSets.setOnClickListener {
            // Show set selection dialog
            val setDialog = SetSelectionDialog(selectedSets) { sets ->
                selectedSets.clear()
                selectedSets.addAll(sets)
                updateSelectedSetsChips()
            }
            setDialog.show(parentFragmentManager, "SetSelectionDialog")
        }
    }

    private fun updateSelectedSetsChips() {
        binding.cgSelectedSets.removeAllViews()

        if (selectedSets.isEmpty()) {
            val chip = Chip(requireContext()).apply {
                text = "No sets selected"
                isEnabled = false
            }
            binding.cgSelectedSets.addView(chip)
        } else {
            selectedSets.forEach { setCode ->
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
        }
    }

    private fun setupRangeFilters() {
        // Mana Value
        currentFilters.manaValueMin?.let {
            binding.etManaValueMin.setText(it.toString())
        }
        currentFilters.manaValueMax?.let {
            binding.etManaValueMax.setText(it.toString())
        }

        // Price
        currentFilters.priceMin?.let {
            binding.etPriceMin.setText(it.toString())
        }
        currentFilters.priceMax?.let {
            binding.etPriceMax.setText(it.toString())
        }

        // Year
        currentFilters.yearMin?.let {
            binding.etYearMin.setText(it.toString())
        }
        currentFilters.yearMax?.let {
            binding.etYearMax.setText(it.toString())
        }
    }

    private fun setupTextFilters() {
        binding.etArtist.setText(currentFilters.artist)
        binding.etOracleText.setText(currentFilters.oracleText)
        binding.etFlavorText.setText(currentFilters.flavorText)
    }

    private fun setupListeners() {
        binding.btnApply.setOnClickListener {
            val filters = SearchFilters(
                colors = colorFilters.toMap(),
                sets = selectedSets.toSet(),
                cardTypes = selectedCardTypes.toSet(),
                rarities = selectedRarities.toSet(),
                manaValueMin = binding.etManaValueMin.text.toString().toIntOrNull(),
                manaValueMax = binding.etManaValueMax.text.toString().toIntOrNull(),
                priceMin = binding.etPriceMin.text.toString().toFloatOrNull(),
                priceMax = binding.etPriceMax.text.toString().toFloatOrNull(),
                artist = binding.etArtist.text.toString().trim(),
                yearMin = binding.etYearMin.text.toString().toIntOrNull(),
                yearMax = binding.etYearMax.text.toString().toIntOrNull(),
                oracleText = binding.etOracleText.text.toString().trim(),
                flavorText = binding.etFlavorText.text.toString().trim()
            )
            onFiltersApplied(filters)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnClearAll.setOnClickListener {
            clearAllFilters()
        }
    }

    private fun clearAllFilters() {
        // Clear colors
        colorFilters.clear()
        colorAdapter.colorItems.forEach { it.state = ColorFilterState.NEUTRAL }
        colorAdapter.notifyDataSetChanged()

        // Clear card types
        selectedCardTypes.clear()
        binding.cgCardTypes.children.filterIsInstance<Chip>().forEach {
            it.isChecked = false
        }

        // Clear rarities
        selectedRarities.clear()
        binding.cgRarity.children.filterIsInstance<Chip>().forEach {
            it.isChecked = false
        }

        // Clear sets
        selectedSets.clear()
        updateSelectedSetsChips()

        // Clear ranges
        binding.etManaValueMin.text?.clear()
        binding.etManaValueMax.text?.clear()
        binding.etPriceMin.text?.clear()
        binding.etPriceMax.text?.clear()
        binding.etYearMin.text?.clear()
        binding.etYearMax.text?.clear()

        // Clear text fields
        binding.etArtist.text?.clear()
        binding.etOracleText.text?.clear()
        binding.etFlavorText.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
