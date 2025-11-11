package screen.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentWelcomeBinding
import kotlinx.coroutines.launch

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {


    private val viewModel: AuthViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            markOnboardedAndNavigate(R.id.action_welcomeFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener {
            markOnboardedAndNavigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }

    private fun markOnboardedAndNavigate(actionId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setOnboarded(true)
            findNavController().navigate(actionId)
        }
    }

}
