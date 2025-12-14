package com.example.mtgcollectionmanager.presentation.screen.details

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.FragmentCardDetailsBinding
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.loadImage
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.common.showErrorSnackbar
import com.example.mtgcollectionmanager.presentation.common.showSuccessSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardDetailsFragment : BaseFragment<FragmentCardDetailsBinding>(
    FragmentCardDetailsBinding::inflate
) {
    private val viewModel: CardDetailsViewModel by viewModels()
    private val args: CardDetailsFragmentArgs by navArgs()

    override fun bind() {
        viewModel.onEvent(CardDetailsContract.Event.LoadCard(args.cardId))
        setupSpinners()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            etNotes.doAfterTextChanged { text ->
                viewModel.onEvent(CardDetailsContract.Event.NotesChanged(text.toString()))
            }

            btnAddToCollection.setOnClickListener {
                viewModel.onEvent(CardDetailsContract.Event.AddToCollectionClicked)
            }

            btnViewOtherPrintings.setOnClickListener {
                showPrintingsDialog()
            }
        }
    }

    private fun showPrintingsDialog() {
        val cardName = viewModel.state.value.card?.name ?: return
        val currentCardId = viewModel.state.value.card?.cardId

        val dialog = com.example.mtgcollectionmanager.presentation.screen.common.dialog.CardPrintingsDialog(
            cardName = cardName,
            currentCardId = currentCardId,
            onPrintingSelected = { newCardId ->
                // Reload the card details with the new printing
                viewModel.onEvent(CardDetailsContract.Event.LoadCard(newCardId))
            }
        )
        dialog.show(parentFragmentManager, "CardPrintingsDialog")
    }

    private fun setupSpinners() {
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
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.onEvent(CardDetailsContract.Event.QuantityChanged(quantities[position]))
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
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.onEvent(CardDetailsContract.Event.ConditionSelected(conditions[position]))
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        if (state.isLoading) {
                            progressBar.show()
                        } else {
                            progressBar.hide()
                        }

                        state.card?.let { card ->
                            ivCardImage.loadImage(card.imageUrl)
                            tvCardName.text = card.name
                            // Render mana symbols using the helper
                            com.example.mtgcollectionmanager.presentation.common.ManaSymbolRenderer.renderManaSymbols(
                                requireContext(),
                                card.manaCost,
                                llManaSymbols
                            )
                            tvType.text = getString(R.string.type) + ": " + card.type
                            tvSetInfo.text = getString(R.string.set) + ": " + card.setInfo
                            tvPrice.text = card.priceFormatted
                        }

                        if (state.isInCollection) {
                            btnAddToCollection.isEnabled = false
                            btnAddToCollection.text = getString(R.string.already_in_collection)
                        } else {
                            btnAddToCollection.isEnabled = true
                            btnAddToCollection.text = getString(R.string.add_to_collection)
                        }
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is CardDetailsContract.SideEffect.NavigateBack -> {
                            findNavController().popBackStack()
                        }
                        is CardDetailsContract.SideEffect.ShowAddedToCollection -> {
                            binding.root.showSuccessSnackbar(getString(R.string.card_added_success))
                        }
                        is CardDetailsContract.SideEffect.ShowError -> {
                            binding.root.showErrorSnackbar(sideEffect.message.asString(requireContext()))
                        }
                    }
                }
            }
        }
    }
}
