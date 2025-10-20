package com.example.sababukia_tbc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sababukia_tbc.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            binding.activeUsersText.text = getString(R.string.active_users_count, users.size)
        }

        userViewModel.snackbarMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { (message, isSuccess) ->
                showSnackbar(message, isSuccess)
            }
        }
    }

    private fun setupClickListeners() {
        binding.addUserBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addUserFragment)
        }

        binding.updateUserBtn.setOnClickListener {
            handleUpdateUserClick()
        }
    }

    private fun handleUpdateUserClick() {
        val users = userViewModel.users.value
        when {
            users.isNullOrEmpty() -> {
                showSnackbar(getString(R.string.user_list_is_empty), false)
            }

            else -> {
                val randomUser = users.random()
                val action = MainFragmentDirections.actionMainFragmentToAddUserFragment(randomUser)
                findNavController().navigate(action)
            }
        }
    }


    private fun showSnackbar(message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (isSuccess) {
            snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
        }
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
