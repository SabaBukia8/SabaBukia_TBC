package com.example.sababukia_tbc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityRegisterSecondStepBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterSecondStep : AppCompatActivity() {
    lateinit var binding: ActivityRegisterSecondStepBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityRegisterSecondStepBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(binding.registerSecond) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.nextButton.setOnClickListener {
            val nickname = binding.nicknameEditText.text.toString()

            if (nickname.isNotEmpty()) {
                val user = auth.currentUser
                if (user != null) {
                    val profileUpdates =
                        UserProfileChangeRequest.Builder().setDisplayName(nickname).build()

                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this, getString(R.string.nickname_is_set), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.couldn_t_set_a_nickname),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this, getString(R.string.you_are_not_signed_in), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.nicknameEditText.error = getString(R.string.please_enter_a_nickname)
            }
        }
    }
}