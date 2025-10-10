package com.example.sababukia_tbc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityRegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterPage : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(binding.registerTitle) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.nextButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (isValidEmail(email) && isValidPassword(password)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.authentication_successful), Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, RegisterSecondStep::class.java)
                        startActivity(intent)

                    } else {
                        val exception = task.exception
                        Log.e(
                            getString(R.string.registerpage),
                            getString(R.string.authentication_failed),
                            exception
                        )
                        val errorMessage = when (exception) {
                            is FirebaseAuthUserCollisionException -> getString(R.string.email_already_used)
                            else -> getString(R.string.authentication_failedOne, exception?.message)
                        }
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.emailEditText.error = getString(R.string.please_enter_an_email)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = getString(R.string.please_enter_a_correct_email)
            return false
        }
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.passwordEditText.error = getString(R.string.please_enter_a_password)
            return false
        }

        if (password.length < 6) {
            binding.passwordEditText.error =
                getString(R.string.password_must_be_at_least_8_characters_long)
            return false
        }
        return true
    }
}