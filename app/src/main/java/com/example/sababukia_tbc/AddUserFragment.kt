package com.example.sababukia_tbc

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sababukia_tbc.databinding.FragmentAddUserBinding

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val args: AddUserFragmentArgs by navArgs()
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = args.user

        if (currentUser != null) {
            binding.etFirstName.setText(currentUser!!.firstName)
            binding.etLastName.setText(currentUser!!.lastName)
            binding.etAge.setText(currentUser!!.age)
            binding.etEmail.setText(currentUser!!.email)
            binding.etEmail.isEnabled = false
            binding.btnAddUser.text = getString(R.string.update_user)
            binding.btnRemoveUser.isVisible = true
        } else {
            binding.btnAddUser.text = getString(R.string.add_user)
            binding.btnRemoveUser.isVisible = false
        }

        binding.btnAddUser.setOnClickListener {
            if (validateInput()) {
                val firstName = binding.etFirstName.text.toString().trim()
                val lastName = binding.etLastName.text.toString().trim()
                val age = binding.etAge.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()

                if (currentUser != null) {
                    val updatedUser = User(firstName, lastName, age, email)
                    userViewModel.updateUser(updatedUser)
                } else {
                    val newUser = User(firstName, lastName, age, email)
                    userViewModel.addUser(newUser)
                }
                findNavController().popBackStack()
            }
        }

        binding.btnRemoveUser.setOnClickListener {
            currentUser?.let { user ->
                userViewModel.removeUser(user)
                findNavController().popBackStack()
            }
        }
    }

    private fun validateInput(): Boolean {
        with(binding) {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val age = etAge.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (firstName.isEmpty()) {
                etFirstName.error = getString(R.string.please_enter_your_first_name)
                return false
            }

            if (lastName.isEmpty()) {
                etLastName.error = getString(R.string.please_enter_a_valid_last_name)
                return false
            }

            val ageInt = age.toIntOrNull()
            if (age.isEmpty() || ageInt == null || ageInt <= 0 || ageInt > 150) {
                etAge.error = getString(R.string.please_enter_a_valid_age)
                return false
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = getString(R.string.please_enter_a_valid_email_address)
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
