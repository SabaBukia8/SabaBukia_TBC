package com.example.mtgcollectionmanager.presentation.screen.search.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.model.remote.SetDto
import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.databinding.DialogSetFilterBinding
import com.example.mtgcollectionmanager.databinding.ItemSetSelectionBinding
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.model.SetUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SetSelectionDialog(
    selectedSets: Set<String>,
    private val onSetsSelected: (Set<String>) -> Unit
) : DialogFragment() {

    private var _binding: DialogSetFilterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiService: ScryfallApiService

    private val selectedSetCodes = selectedSets.toMutableSet()
    private var allSets: List<SetUiModel> = emptyList()
    private lateinit var adapter: SetSelectionAdapter

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
        adapter = SetSelectionAdapter(selectedSetCodes) { setCode, isChecked ->
            if (isChecked) {
                selectedSetCodes.add(setCode)
            } else {
                selectedSetCodes.remove(setCode)
            }
        }

        binding.rvSets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SetSelectionDialog.adapter
        }
    }

    private fun setupListeners() = with(binding) {
        etSearch.addTextChangedListener { text ->
            filterSets(text.toString())
        }

        btnApply.setOnClickListener {
            onSetsSelected(selectedSetCodes)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnClearAll.setOnClickListener {
            // Simply clear the selected sets and refresh the list
            selectedSetCodes.clear()
            adapter.submitList(allSets.toList())
        }
    }

    private fun loadSets() = with(binding) {
        progressBar.show()
        rvSets.hide()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getSets()
                if (response.isSuccessful && response.body() != null) {
                    allSets = response.body()!!.data.map { it.toUiModel() }
                        .sortedByDescending { it.releasedAt }
                    adapter.submitList(allSets)
                    progressBar.hide()
                    rvSets.show()
                } else {
                    progressBar.hide()
                    tvNoResults.text = response.message() ?: "Failed to load sets"
                    tvNoResults.show()
                }
            } catch (e: Exception) {
                progressBar.hide()
                tvNoResults.text = e.message ?: "Failed to load sets"
                tvNoResults.show()
            }
        }
    }

    private fun filterSets(query: String) = with(binding) {
        val filteredList = if (query.isBlank()) {
            allSets
        } else {
            allSets.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true)
            }
        }
        
        adapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            tvNoResults.show()
            rvSets.hide()
        } else {
            tvNoResults.hide()
            rvSets.show()
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

    class SetSelectionAdapter(
        private val selectedSets: MutableSet<String>,
        private val onSetToggled: (String, Boolean) -> Unit
    ) : ListAdapter<SetUiModel, SetSelectionAdapter.SetViewHolder>(SetDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
            val binding = ItemSetSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SetViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class SetViewHolder(
            private val binding: ItemSetSelectionBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(set: SetUiModel) = with(binding) {
                val isSelected = selectedSets.contains(set.code)
                cbSet.text = itemView.context.getString(
                    R.string.cards, 
                    set.name, 
                    set.code, 
                    set.cardCount
                )
                cbSet.isChecked = isSelected

                cbSet.setOnCheckedChangeListener { _, isChecked ->
                    onSetToggled(set.code, isChecked)
                }
            }
        }

        private class SetDiffCallback : DiffUtil.ItemCallback<SetUiModel>() {
            override fun areItemsTheSame(oldItem: SetUiModel, newItem: SetUiModel): Boolean {
                return oldItem.code == newItem.code
            }

            override fun areContentsTheSame(oldItem: SetUiModel, newItem: SetUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}