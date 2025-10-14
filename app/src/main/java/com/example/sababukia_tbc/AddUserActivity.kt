package com.example.sababukia_tbc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sababukia_tbc.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private var existingUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUp()
    }

    private fun setUp() {
        val firstName = intent.getStringExtra(getString(R.string.user_first_name_key))
        val lastName = intent.getStringExtra(getString(R.string.user_last_name_key))
        val age = intent.getStringExtra(getString(R.string.user_age_key))
        val email = intent.getStringExtra(getString(R.string.user_email_key))

        if (firstName != null && lastName != null && age != null && email != null) {
            existingUser = User(firstName, lastName, age, email)
        }
        if (existingUser != null) {
            with(binding) {
                etFirstName.setText(existingUser?.firstName)
                etLastName.setText(existingUser?.lastName)
                etAge.setText(existingUser?.age)
                etEmail.setText(existingUser?.email)
                etEmail.isEnabled = false
                btnAddUser.text = getString(R.string.update_user)
                btnRemoveUser.visibility = View.VISIBLE
            }
        }
        listeners()
    }

    private fun listeners() {
        binding.btnAddUser.setOnClickListener {
            addUserFun()
        }

        binding.btnRemoveUser.setOnClickListener {
            removeUserFun()
        }
    }

    private fun addUserFun() = with(binding) {
        if (validateInput()) {
            val resultIntent = Intent()
            resultIntent.putExtra(
                getString(R.string.user_first_name_key),
                etFirstName.text.toString().trim()
            )
            resultIntent.putExtra(
                getString(R.string.user_last_name_key),
                etLastName.text.toString().trim()
            )
            resultIntent.putExtra(
                getString(R.string.user_age_key),
                etAge.text.toString().trim()
            )
            resultIntent.putExtra(
                getString(R.string.user_email_key),
                etEmail.text.toString().trim()
            )

            if (existingUser != null) {
                resultIntent.putExtra(
                    getString(R.string.action_key),
                    getString(R.string.update_action)
                )
            } else {
                resultIntent.putExtra(
                    getString(R.string.action_key),
                    getString(R.string.add_action)
                )
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun removeUserFun() {
        val resultIntent = Intent()
        resultIntent.putExtra(getString(R.string.user_first_name_key), existingUser?.firstName)
        resultIntent.putExtra(getString(R.string.user_last_name_key), existingUser?.lastName)
        resultIntent.putExtra(getString(R.string.user_age_key), existingUser?.age)
        resultIntent.putExtra(getString(R.string.user_email_key), existingUser?.email)
        resultIntent.putExtra(getString(R.string.action_key), getString(R.string.delete_action))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
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
}