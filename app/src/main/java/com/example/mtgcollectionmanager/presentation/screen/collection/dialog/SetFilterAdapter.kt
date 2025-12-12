package com.example.mtgcollectionmanager.presentation.screen.collection.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.databinding.ItemSetFilterBinding
import com.example.mtgcollectionmanager.presentation.model.SetUiModel
import com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract

class SetFilterAdapter(
    private val filterStates: MutableMap<String, CollectionContract.FilterState>,
    private val onStateChanged: (String, CollectionContract.FilterState) -> Unit
) : ListAdapter<SetUiModel, SetFilterAdapter.SetViewHolder>(SetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val binding = ItemSetFilterBinding.inflate(
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
        private val binding: ItemSetFilterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(set: SetUiModel) {
            with(binding) {
                tvSetName.text = "${set.name} (${set.code}) - ${set.cardCount} cards"

                // Update icon based on current state
                val currentState = filterStates[set.code] ?: CollectionContract.FilterState.NEUTRAL
                updateStateIcon(currentState)

                root.setOnClickListener {
                    // Read the CURRENT state from the map, not the captured value
                    val currentStateNow = filterStates[set.code] ?: CollectionContract.FilterState.NEUTRAL

                    // Cycle through states: NEUTRAL -> INCLUDE -> EXCLUDE -> NEUTRAL
                    val newState = when (currentStateNow) {
                        CollectionContract.FilterState.NEUTRAL -> CollectionContract.FilterState.INCLUDE
                        CollectionContract.FilterState.INCLUDE -> CollectionContract.FilterState.EXCLUDE
                        CollectionContract.FilterState.EXCLUDE -> CollectionContract.FilterState.NEUTRAL
                    }
                    filterStates[set.code] = newState
                    updateStateIcon(newState)
                    onStateChanged(set.code, newState)
                }
            }
        }

        private fun updateStateIcon(state: CollectionContract.FilterState) {
            with(binding.ivFilterState) {
                when (state) {
                    CollectionContract.FilterState.NEUTRAL -> {
                        setImageDrawable(null)
                    }
                    CollectionContract.FilterState.INCLUDE -> {
                        setImageResource(com.example.mtgcollectionmanager.R.drawable.ic_check_mark)
                        clearColorFilter()
                    }
                    CollectionContract.FilterState.EXCLUDE -> {
                        setImageResource(com.example.mtgcollectionmanager.R.drawable.ic_redx)
                        clearColorFilter()
                    }
                }
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
