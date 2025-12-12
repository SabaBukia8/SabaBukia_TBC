package com.example.mtgcollectionmanager.presentation.screen.common.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.DialogCardPrintingsBinding
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardPrintingsDialog(
    private val cardName: String,
    private val currentCardId: String? = null,
    private val onPrintingSelected: (String) -> Unit  // cardId
) : DialogFragment() {

    private var _binding: DialogCardPrintingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CardPrintingsViewModel by viewModels()

    private val adapter by lazy {
        CardPrintingsAdapter(currentCardId) { cardId ->
            onPrintingSelected(cardId)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MTGCollectionManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCardPrintingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        observeState()
        setupListeners()

        // Load printings
        viewModel.loadPrintings(cardName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.8).toInt()
        )
        return dialog
    }

    private fun setupUI() {
        binding.tvCardName.text = cardName
    }

    private fun setupRecyclerView() {
        binding.rvPrintings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CardPrintingsDialog.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        when {
                            state.isLoading -> {
                                progressBar.show()
                                rvPrintings.hide()
                                tvError.hide()
                            }
                            state.error != null -> {
                                progressBar.hide()
                                rvPrintings.hide()
                                tvError.text = state.error
                                tvError.show()
                            }
                            state.printings.isNotEmpty() -> {
                                progressBar.hide()
                                tvError.hide()
                                rvPrintings.show()
                                adapter.submitList(state.printings)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
