package screen.auth

import android.os.Bundle
import android.view.View
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentSuccessBinding

class SuccessFragment : BaseFragment<FragmentSuccessBinding>(FragmentSuccessBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {
            // Handle continue action - could navigate to main app or close
            requireActivity().finish()
        }
    }
}