package com.example.mtgcollectionmanager.presentation.screen.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mtgcollectionmanager.databinding.FragmentSplashBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(
    FragmentSplashBinding::inflate
) {
    private val viewModel: SplashViewModel by viewModels()

    override fun bind() {
        observeSideEffects()
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is SplashContract.SideEffect.NavigateToLogin -> {
                            findNavController().navigate(
                                SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                            )
                        }
                        is SplashContract.SideEffect.NavigateToCollectionsList -> {
                            findNavController().navigate(
                                SplashFragmentDirections.actionSplashFragmentToCollectionsListFragment()
                            )
                        }
                    }
                }
            }
        }
    }
}
