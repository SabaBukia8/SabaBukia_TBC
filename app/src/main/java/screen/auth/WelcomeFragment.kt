package screen.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            navigateToRegister()
        }
        binding.btnLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToRegister() {
        try {
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToLogin() {
        try {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
