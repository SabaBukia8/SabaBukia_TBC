package com.example.sababukia_tbc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sababukia_tbc.databinding.ActivityAddUserBinding
import com.google.android.material.snackbar.Snackbar
import java.util.UUID

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.saveButton.setOnClickListener {
            with(binding) {
                val firstName = firstNameEditText.text.toString()
                val lastName = lastNameEditText.text.toString()
                val birthday = birthdayEditText.text.toString()
                val address = addressEditText.text.toString()
                val email = emailEditText.text.toString()

                if (firstName.isNotEmpty() && lastName.isNotEmpty() && birthday.isNotEmpty() && address.isNotEmpty() && email.isNotEmpty()) {
                    val user = User(
                        id = UUID.randomUUID().toString(),
                        firstName = firstName,
                        lastName = lastName,
                        birthday = birthday.toLong(),
                        email = email,
                        address = address
                    )
                    val resultIntent = Intent().apply {
                        putExtra("user", user)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Snackbar.make(root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
