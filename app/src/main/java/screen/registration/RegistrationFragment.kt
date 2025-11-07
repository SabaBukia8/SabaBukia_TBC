package screen.registration

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentRegistrationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import util.DatePickerUtil

class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var sectionAdapter: FormSectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
        observeChooserDialog()
    }

    private fun setupRecyclerView() {
        sectionAdapter = FormSectionAdapter(
            onFieldValueChanged = viewModel::updateFieldValue,
            onChooserClick = viewModel::onChooserClicked,
            getFieldValue = viewModel::getFieldValue
        )

        binding.rvFormSections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sectionAdapter
        }
    }

    override fun listeners() = with(binding) {
        btnRegister.setOnClickListener {
            handleRegistration()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RegistrationUiState.Loading -> showLoading(true)
                        is RegistrationUiState.Success -> {
                            showLoading(false)
                            sectionAdapter.submitList(state.sections)
                        }

                        is RegistrationUiState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeChooserDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chooserDialog.collect { dialogState ->
                    when (dialogState) {
                        is ChooserDialog.Show -> showChooser(dialogState)
                        is ChooserDialog.Hide -> {}
                    }
                }
            }
        }
    }

    private fun showChooser(dialogState: ChooserDialog.Show) {
        when {
            dialogState.title.contains("birthday", ignoreCase = true) -> {
                DatePickerUtil.showDatePicker(
                    context = requireContext(),
                    currentDate = viewModel.getFieldValue(dialogState.fieldId)
                        .takeIf { it.isNotBlank() }
                ) { selectedDate ->
                    viewModel.onChooserOptionSelected(dialogState.fieldId, selectedDate)
                }
            }

            else -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(dialogState.title)
                    .setItems(dialogState.options.toTypedArray()) { _, which ->
                        viewModel.onChooserOptionSelected(
                            dialogState.fieldId,
                            dialogState.options[which]
                        )
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ -> viewModel.hideChooserDialog() }
                    .setOnCancelListener { viewModel.hideChooserDialog() }
                    .show()
            }
        }
    }

    private fun handleRegistration() {
        when (val result = viewModel.validateAndSubmit()) {
            is ValidationResult.Valid -> {
                showSuccess(getString(R.string.registration_successful))
            }

            is ValidationResult.Invalid -> {
                showError(result.message)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.rvFormSections.isVisible = !isLoading
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}