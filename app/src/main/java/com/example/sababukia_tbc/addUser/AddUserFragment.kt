package com.example.sababukia_tbc.addUser

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentAddUserBinding
import com.google.android.material.snackbar.Snackbar
import com.example.sababukia_tbc.userPage.User
import com.example.sababukia_tbc.userPage.Users

typealias Str = R.string

class AddUserFragment : BaseFragment<FragmentAddUserBinding>(FragmentAddUserBinding::inflate) {

    private val args: AddUserFragmentArgs by navArgs()
    private var currentUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentUser = args.user
        super.onViewCreated(view, savedInstanceState)
    }

    override fun listeners() {
        binding.saveButton.setOnClickListener {
            handleSave()
        }

        binding.deleteButton.setOnClickListener {
            handleDelete()
        }
    }

    override fun bind() {
        binding.apply {
            currentUser?.let { user ->
                firstNameEditText.setText(user.firstName)
                lastNameEditText.setText(user.lastName)
                ageEditText.setText(user.age.toString())
                emailEditText.setText(user.email)
                emailEditText.isEnabled = false
                deleteButton.visibility = View.VISIBLE
                saveButton.text = getString(R.string.update)
            } ?: run {
                saveButton.text = getString(R.string.save)
            }
        }
    }

    private fun handleSave() {
        binding.apply {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            val validationError = validateInput(firstName, lastName, age, email)
            if (validationError != null) {
                showSnackbar(getString(validationError), false)
                return
            }

            val bundle = Bundle().apply {
                putBoolean(getString(Str.is_success), true) // Use is_success key for boolean
                if (currentUser != null) {
                    val updatedUser = currentUser!!.copy(
                        firstName = firstName,
                        lastName = lastName,
                        age = age.toInt()
                    )
                    Users.updateUser(updatedUser)
                    putString(
                        getString(Str.result_message),
                        getString(R.string.success_user_updated)
                    )
                } else {
                    val newUser = User(firstName, lastName, age.toInt(), email)
                    Users.addUser(newUser)
                    putString(getString(Str.result_message), getString(R.string.success_user_added))
                }
            }
            setFragmentResult(getString(Str.add_edit_user_request), bundle)
            findNavController().popBackStack()
        }
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        age: String,
        email: String
    ): Int? {
        if (firstName.isBlank() || lastName.isBlank() || age.isBlank() || email.isBlank()) {
            return R.string.error_fill_all_fields
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return R.string.error_invalid_email
        }
        if (currentUser == null && Users.getUsers()
                .any { it.email.equals(email, ignoreCase = true) }
        ) {
            return R.string.error_email_exists
        }
        return null
    }

    private fun handleDelete() {
        currentUser?.let { user ->
            Users.removeUser(user)
            val bundle = Bundle().apply {
                putBoolean(getString(Str.is_success), true)
                putString(getString(Str.result_message), getString(R.string.success_user_deleted))
            }
            setFragmentResult(getString(Str.add_edit_user_request), bundle)
            findNavController().popBackStack()
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

}
