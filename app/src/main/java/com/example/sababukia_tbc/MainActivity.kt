package com.example.sababukia_tbc

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val users = mutableListOf<User>()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        registerActivityResult()
        listeners()
    }

    private fun listeners() {
        binding.addUserBtn.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.updateUserBtn.setOnClickListener {
            if (users.isEmpty()) {
                showSnackbar(getString(R.string.user_list_is_empty), false)
            } else {
                val randomUser = users.random()
                val intent = Intent(this, AddUserActivity::class.java)
                intent.putExtra(getString(R.string.user_first_name_key), randomUser.firstName)
                intent.putExtra(getString(R.string.user_last_name_key), randomUser.lastName)
                intent.putExtra(getString(R.string.user_age_key), randomUser.age)
                intent.putExtra(getString(R.string.user_email_key), randomUser.email)
                resultLauncher.launch(intent)
            }
        }
    }

    private fun registerActivityResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        val firstName = data.getStringExtra(getString(R.string.user_first_name_key))
                        val lastName = data.getStringExtra(getString(R.string.user_last_name_key))
                        val age = data.getStringExtra(getString(R.string.user_age_key))
                        val email = data.getStringExtra(getString(R.string.user_email_key))
                        val action = data.getStringExtra(getString(R.string.action_key))

                        if (firstName != null && lastName != null && age != null && email != null && action != null) {
                            val user = User(firstName, lastName, age, email)
                            when (action) {
                                getString(R.string.add_action) -> {
                                    if (users.any {
                                            it.email.equals(
                                                user.email,
                                                ignoreCase = true
                                            )
                                        }) {
                                        showSnackbar(getString(R.string.user_already_exists), false)
                                    } else {
                                        users.add(user)
                                        updateActiveUsersCount()
                                        showSnackbar(
                                            getString(R.string.user_added_successfully),
                                            true
                                        )
                                    }
                                }

                                getString(R.string.update_action) -> {
                                    val index = users.indexOfFirst {
                                        it.email.equals(
                                            user.email,
                                            ignoreCase = true
                                        )
                                    }
                                    if (index != -1) {
                                        users[index] = user
                                        showSnackbar(
                                            getString(R.string.user_updated_successfully),
                                            true
                                        )
                                    } else {
                                        showSnackbar(getString(R.string.user_does_not_exist), false)
                                    }
                                }

                                getString(R.string.delete_action) -> {
                                    val removed = users.removeIf {
                                        it.email.equals(
                                            user.email,
                                            ignoreCase = true
                                        )
                                    }
                                    if (removed) {
                                        updateActiveUsersCount()
                                        showSnackbar(
                                            getString(R.string.user_deleted_successfully),
                                            true
                                        )
                                    } else {
                                        showSnackbar(getString(R.string.user_does_not_exist), false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun updateActiveUsersCount() {
        binding.activeUsersText.text = getString(R.string.active_users_count, users.size)
    }

    private fun showSnackbar(message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (isSuccess) {
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green))
        } else {
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red))
        }
        snackbar.show()
    }
}