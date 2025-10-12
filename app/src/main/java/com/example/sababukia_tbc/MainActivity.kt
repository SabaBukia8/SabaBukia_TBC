package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


data class User(var firstName: String, var secondName: String, var age: String, var email: String)
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val users = mutableListOf<User>()
    private var deletedUsersCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        setupClickListeners()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.addUserBtn.setOnClickListener {
            addUser()
        }
        binding.RemoveUserBtn.setOnClickListener {
            removeUser()
        }
        binding.UpdateUserBtn.setOnClickListener {
            updateUser()
        }
        binding.showInfoBtn.setOnClickListener {
            showUserInfo()
        }
    }

    private fun addUser() {
        with(binding) {
            val userFirstName = enterFirstName.text.toString().trim()
            val userLastName = enterLastname.text.toString().trim()
            val userAge = enterAge.text.toString().trim()
            val userEmail = enterEmail.text.toString().trim()
            if (userFirstName.isEmpty()) {
                enterFirstName.error = getString(R.string.please_enter_your_first_name)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            if (userLastName.isEmpty()) {
                enterLastname.error = getString(R.string.please_enter_a_valid_last_name)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail)
                    .matches()
            ) {
                enterEmail.error = getString(R.string.please_enter_a_valid_email_address)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            val age = userAge.toIntOrNull()
            if (age == null || age <= 0 || age > 150) {
                enterAge.error = getString(R.string.please_enter_a_valid_age)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            if (userExists(userEmail)) {
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                Snackbar.make(root, getString(R.string.user_already_exists), Snackbar.LENGTH_LONG)
                    .show()
                return
            }

            val user = User(userFirstName, userLastName, userAge, userEmail)
            users.add(user)
            operationsValidity.text = getString(R.string.user_added_successfully)
            operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.green))
            Snackbar.make(root, getString(R.string.user_added_successfully), Snackbar.LENGTH_LONG).show()


            activeUsersText.text = getString(R.string.users, users.size)

            clearInputFields()
        }
    }

    private fun removeUser() {
        with(binding) {
            val userEmail = enterEmail.text.toString().trim()

            if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                enterEmail.error = getString(R.string.please_enter_a_valid_email_address)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            val user = users.find { it.email.equals(userEmail, ignoreCase = true) }

            if (user != null) {
                users.remove(user)
                deletedUsersCount++
                operationsValidity.text = getString(R.string.user_deleted_successfully)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.green))
                activeUsersText.text = getString(R.string.users, users.size)
                deletedUsersText.text = getString(R.string.deleted_users, deletedUsersCount)
                clearInputFields()
            } else {
                operationsValidity.text = getString(R.string.user_not_found)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
            }
        }
    }

    private fun updateUser() {
        with(binding) {
            val userEmail = enterEmail.text.toString().trim()

            if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                enterEmail.error = getString(R.string.please_enter_a_valid_email_address)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            val user = users.find { it.email.equals(userEmail, ignoreCase = true) }

            if (user != null) {
                val userFirstName = enterFirstName.text.toString().trim()
                val userLastName = enterLastname.text.toString().trim()
                val userAge = enterAge.text.toString().trim()

                if (userFirstName.isNotEmpty()) {
                    user.firstName = userFirstName
                }
                if (userLastName.isNotEmpty()) {
                    user.secondName = userLastName
                }
                if (userAge.isNotEmpty()) {
                    user.age = userAge
                }

                operationsValidity.text = getString(R.string.user_updated_successfully)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.green))
                clearInputFields()
            } else {
                operationsValidity.text = getString(R.string.user_not_found)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
            }
        }
    }

    private fun showUserInfo() {
        with(binding) {
            val userEmail = enterEmail.text.toString().trim()

            if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                enterEmail.error = getString(R.string.please_enter_a_valid_email_address)
                operationsValidity.text = getString(R.string.error)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                return
            }

            val user = users.find { it.email.equals(userEmail, ignoreCase = true) }

            if (user != null) {
                userInfoDisplay.text = getString(R.string.user_info_format, user.firstName, user.secondName, user.age, user.email)
                operationsValidity.text = getString(R.string.user_info_displayed)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.green))
            } else {
                operationsValidity.text = getString(R.string.user_not_found)
                operationsValidity.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                userInfoDisplay.text = ""
            }
        }
    }

    private fun clearInputFields() {
        binding.apply {
            enterFirstName.text?.clear()
            enterLastname.text?.clear()
            enterEmail.text?.clear()
            enterAge.text?.clear()
        }
    }
    private fun userExists(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }
}