package com.example.sababukia_tbc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sababukia_tbc.databinding.ActivityAddUserBinding
import com.google.android.material.snackbar.Snackbar

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
                    val existingIds = intent.getStringArrayListExtra(getString(R.string.existing_ids_key)) ?: arrayListOf()
                    val newId = generateNewId(existingIds)

                    val user = User(
                        id = newId,
                        firstName = firstName,
                        lastName = lastName,
                        birthday = birthday.toLong(),
                        email = email,
                        address = address
                    )
                    val resultIntent = Intent().apply {
                        putExtra(getString(R.string.user_key), user)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Snackbar.make(root, getString(R.string.please_fill_all_fields), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateNewId(existingIds: ArrayList<String>): String {
        val existingIdInts = existingIds.mapNotNull { it.toIntOrNull() }.sorted()
        var newId = 1
        for (id in existingIdInts) {
            if (id == newId) {
                newId++
            } else {
                break
            }
        }
        return newId.toString()
    }
}
