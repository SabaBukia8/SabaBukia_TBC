package com.example.sababukia_tbc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etAge: TextInputEditText

    private lateinit var emailLayout: TextInputLayout
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var ageLayout: TextInputLayout

    private lateinit var btnSave: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var btnAgain: MaterialButton

    private lateinit var inputSection: LinearLayoutCompat
    private lateinit var displaySection: LinearLayoutCompat

    private lateinit var tvDisplayEmail: AppCompatTextView
    private lateinit var tvDisplayUsername: AppCompatTextView
    private lateinit var tvDisplayFullName: AppCompatTextView
    private lateinit var tvDisplayAge: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_main)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etAge = findViewById(R.id.etAge)

        emailLayout = findViewById(R.id.emailLayout)
        usernameLayout = findViewById(R.id.usernameLayout)
        firstNameLayout = findViewById(R.id.firstNameLayout)
        lastNameLayout = findViewById(R.id.lastNameLayout)
        ageLayout = findViewById(R.id.ageLayout)

        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        btnAgain = findViewById(R.id.btnAgain)

        inputSection = findViewById(R.id.inputSection)
        displaySection = findViewById(R.id.displaySection)

        tvDisplayEmail = findViewById(R.id.tvDisplayEmail)
        tvDisplayUsername = findViewById(R.id.tvDisplayUsername)
        tvDisplayFullName = findViewById(R.id.tvDisplayFullName)
        tvDisplayAge = findViewById(R.id.tvDisplayAge)
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateForm()) {
                saveProfile()
            }
        }

        btnClear.setOnLongClickListener {
            clearForm()
            true
        }

        btnAgain.setOnClickListener {
            showInputForm()
        }
    }

    private fun validateForm(): Boolean {
        clearErrors()

        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val age = etAge.text.toString().trim()

        var isValid = true

        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            isValid = false
        }

        if (username.isEmpty()) {
            usernameLayout.error = "Username is required"
            isValid = false
        }

        if (firstName.isEmpty()) {
            firstNameLayout.error = "First name is required"
            isValid = false
        }

        if (lastName.isEmpty()) {
            lastNameLayout.error = "Last name is required"
            isValid = false
        }

        if (age.isEmpty()) {
            ageLayout.error = "Age is required"
            isValid = false
        }

        if (email.isNotEmpty() && !isValidEmail(email)) {
            emailLayout.error = "Please enter a valid email address"
            isValid = false
        }
        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun clearErrors() {
        emailLayout.error = null
        usernameLayout.error = null
        firstNameLayout.error = null
        lastNameLayout.error = null
        ageLayout.error = null
    }

    private fun clearForm() {
        etEmail.text?.clear()
        etUsername.text?.clear()
        etFirstName.text?.clear()
        etLastName.text?.clear()
        etAge.text?.clear()
        clearErrors()
        Toast.makeText(this, "All fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveProfile() {
        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val age = etAge.text.toString().trim()

        tvDisplayEmail.text = "Email: $email"
        tvDisplayUsername.text = "Username: $username"
        tvDisplayFullName.text = "Full Name: $firstName $lastName"
        tvDisplayAge.text = "Age: $age"

        showDisplaySection()

        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun showDisplaySection() {
        inputSection.visibility = View.GONE
        displaySection.visibility = View.VISIBLE
    }

    private fun showInputForm() {
        inputSection.visibility = View.VISIBLE
        displaySection.visibility = View.GONE
    }
}