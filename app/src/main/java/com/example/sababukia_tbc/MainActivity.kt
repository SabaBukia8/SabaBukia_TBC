package com.example.sababukia_tbc

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        updateUI(auth.currentUser != null)

        clickListener()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun clickListener() {
        binding.loginBtnMain.setOnClickListener {
            goToLoginPage()
        }
        binding.registerBtnMain.setOnClickListener {
            goToRegisterPage()
        }
        binding.logoutBtnMain.setOnClickListener {
            logout()
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
    }

    private fun goToRegisterPage() {
        val intent = Intent(this, RegisterPage::class.java)
        startActivity(intent)
    }

    private fun logout() {
        auth.signOut()
        updateUI(false)
    }

    private fun updateUI(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            binding.loginBtnMain.visibility = View.GONE
            binding.registerBtnMain.visibility = View.GONE
            binding.logoutBtnMain.visibility = View.VISIBLE
        } else {
            binding.loginBtnMain.visibility = View.VISIBLE
            binding.registerBtnMain.visibility = View.VISIBLE
            binding.logoutBtnMain.visibility = View.GONE
        }
    }
}