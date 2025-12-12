package com.example.mtgcollectionmanager.presentation.screen.collection.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.model.remote.SetDto
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.databinding.DialogSetFilterBinding
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.model.SetUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SetFilterDialog(
    private val setFilters: Map<String, com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract.FilterState>,
    private val onFiltersApplied: (Map<String, com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract.FilterState>) -> Unit
) : DialogFragment() {

    private var _binding: DialogSetFilterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiService: ScryfallApiService

    private val filterStates = setFilters.toMutableMap()
    private var allSets: List<SetUiModel> = emptyList()
    private lateinit var adapter: SetFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MTGCollectionManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        loadSets()
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
        adapter = SetFilterAdapter(filterStates) { _, _ ->
            // State changes are handled directly in the adapter
        }

        binding.rvSets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SetFilterDialog.adapter
        }
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener { text ->
            filterSets(text.toString())
        }

        binding.btnApply.setOnClickListener {
            val filterMap = filterStates
                .filter { it.value != com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract.FilterState.NEUTRAL }
            onFiltersApplied(filterMap)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnClearAll.setOnClickListener {
            filterStates.clear()
            adapter.submitList(allSets.toList())
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadSets() {
        binding.progressBar.show()
        binding.rvSets.hide()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getSets()
                if (response.isSuccessful && response.body() != null) {
                    allSets = response.body()!!.data.map { it.toUiModel() }
                        .sortedByDescending { it.releasedAt }
                    adapter.submitList(allSets)
                    binding.progressBar.hide()
                    binding.rvSets.show()
                } else {
                    binding.progressBar.hide()
                    binding.tvNoResults.text = response.message() ?: "Failed to load sets"
                    binding.tvNoResults.show()
                }
            } catch (e: Exception) {
                binding.progressBar.hide()
                binding.tvNoResults.text = e.message ?: "Failed to load sets"
                binding.tvNoResults.show()
            }
        }
    }

    private fun filterSets(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allSets)
        } else {
            val filtered = allSets.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true)
            }
            adapter.submitList(filtered)
        }

        if (adapter.currentList.isEmpty()) {
            binding.tvNoResults.show()
            binding.rvSets.hide()
        } else {
            binding.tvNoResults.hide()
            binding.rvSets.show()
        }
    }

    private fun SetDto.toUiModel() = SetUiModel(
        code = code,
        name = name,
        releasedAt = releasedAt ?: "",
        cardCount = cardCount
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
