package com.example.sababukia_tbc.presentation.screen.passcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentPasscodeBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.show
import com.example.sababukia_tbc.presentation.util.ViewUtils.hideViews
import com.example.sababukia_tbc.presentation.util.ViewUtils.showViews
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasscodeFragment : BaseFragment<FragmentPasscodeBinding>(FragmentPasscodeBinding::inflate) {

    private val viewModel: PasscodeViewModel by viewModels()

    private val dotViews by lazy {
        with(binding) {
            arrayOf(dot1, dot2, dot3, dot4)
        }
    }

    private val numberPadViews by lazy {
        with(binding) {
            arrayOf<View>(
                btn0,
                btn1,
                btn2,
                btn3,
                btn4,
                btn5,
                btn6,
                btn7,
                btn8,
                btn9,
                btnDelete,
                btnBiometric
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            btn0.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("0")) }
            btn1.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("1")) }
            btn2.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("2")) }
            btn3.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("3")) }
            btn4.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("4")) }
            btn5.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("5")) }
            btn6.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("6")) }
            btn7.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("7")) }
            btn8.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("8")) }
            btn9.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDigitClick("9")) }

            btnDelete.setOnClickListener { viewModel.onEvent(PasscodeEvent.OnDeleteClick) }

            btnBiometric.setOnClickListener {
                viewModel.authenticateWithBiometric(requireActivity())
            }

            tvForgotPassword.setOnClickListener {
                viewModel.onEvent(PasscodeEvent.OnForgotPasswordClick)
            }

            btnTryAgain.setOnClickListener {
                viewModel.onEvent(PasscodeEvent.OnTryAgainClick)
            }

            if (!viewModel.isBiometricAvailable()) {
                btnBiometric.hide()
            }
        }
    }

    private fun setupListeners() {
        listeners()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updatePasscodeIndicators(state.passcodeDigits.size)

                    with(binding) {
                        if (state.showSuccess) {
                            tvSuccess.show()
                            btnTryAgain.show()
                            hideViews(*dotViews)
                            hideViews(*numberPadViews)
                        } else {
                            tvSuccess.hide()
                            btnTryAgain.hide()
                            showViews(*dotViews)
                            showViews(*numberPadViews)

                            if (!viewModel.isBiometricAvailable()) {
                                btnBiometric.hide()
                            }
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
                        is PasscodeSideEffect.ShowSuccess -> Unit

                        is PasscodeSideEffect.ShowError -> {
                            showSnackbar(
                                sideEffect.message.asString(requireContext()),
                                Snackbar.LENGTH_SHORT
                            )
                        }

                        is PasscodeSideEffect.ShowSnackbar -> {
                            showSnackbar(
                                sideEffect.message.asString(requireContext()),
                                Snackbar.LENGTH_LONG
                            )
                        }

                        is PasscodeSideEffect.ClearPasscode -> {
                            updatePasscodeIndicators(0)
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String, duration: Int) {
        Snackbar.make(binding.root, message, duration).show()
    }

    private fun updatePasscodeIndicators(filledCount: Int) {
        dotViews.forEachIndexed { index, view ->
            if (index < filledCount) {
                view.setBackgroundResource(R.drawable.passcode_dot_filled)
            } else {
                view.setBackgroundResource(R.drawable.passcode_dot_empty)
            }
        }
    }
}
