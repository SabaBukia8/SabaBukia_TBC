package com.example.sababukia_tbc.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.sababukia_tbc.presentation.screen.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private var networkSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNetworkStatus()
        listeners()
        bind()
    }

    private fun observeNetworkStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is MainViewModel.SideEffect.ShowConnectedMessage -> {
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkConnectedSnackbar()
                        }
                        is MainViewModel.SideEffect.ShowDisconnectedMessage -> {
                            networkSnackbar?.dismiss()
                            networkSnackbar = binding.root.showNetworkDisconnectedSnackbar()
                        }
                    }
                }
            }
        }
    }

    open fun listeners() {}

    open fun bind() {}

    override fun onDestroyView() {
        networkSnackbar?.dismiss()
        networkSnackbar = null
        _binding = null
        super.onDestroyView()
    }
}