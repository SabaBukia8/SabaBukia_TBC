package screen.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }

    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationState.collect { destination ->
                    when (destination) {
                        SplashDestination.Home -> navigateToHome()
                        SplashDestination.Welcome -> navigateToWelcome()
                        null -> {}
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } catch (e: Exception) {
            // Fallback to welcome if navigation fails
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        try {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
