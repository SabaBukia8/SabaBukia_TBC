package com.example.sababukia_tbc.presentation.screen.auth

import android.os.Bundle
import android.view.View
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentSuccessBinding

class SuccessFragment : BaseFragment<FragmentSuccessBinding>(FragmentSuccessBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {
            requireActivity().finish()
        }
    }
}