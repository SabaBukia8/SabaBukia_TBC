package com.example.sababukia_tbc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        addUsers()
    }

    private fun addUsers() {
        users.add(User(getString(R.string.user_id_1), getString(R.string.user_firstname_grisha), getString(R.string.user_lastname_oniani), 1724647601641, getString(R.string.user_address_stalins_museum), getString(R.string.user_email_grisha), getString(R.string.grisha_oniani_desc)))
        users.add(User(getString(R.string.user_id_2), getString(R.string.user_firstname_jemal), getString(R.string.user_lastname_kakauridze), 1714647601641, getString(R.string.user_address_lilo), getString(R.string.user_email_jemal), getString(R.string.jemal_kakauridze_desc)))
        users.add(User(getString(R.string.user_id_3), getString(R.string.user_firstname_omger), getString(R.string.user_lastname_kakauridze), 1724647701641, getString(R.string.user_address_asatiani), getString(R.string.user_email_omger), getString(R.string.omger_kakauridze_desc)))
        users.add(User(getString(R.string.user_id_32), getString(R.string.user_firstname_boris), getString(R.string.user_lastname_garuchava), 1714947701641, getString(R.string.user_address_iashvili), getString(R.string.empty_string), getString(R.string.boris_garuchava_desc)))
        users.add(User(getString(R.string.user_id_34), getString(R.string.user_firstname_abto), getString(R.string.user_lastname_sixarulidze), 1714947701641, getString(R.string.user_address_foti), getString(R.string.user_email_abto), getString(R.string.abtho_sikharulidze_desc)))
    }

    private fun listeners() {
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()
                if (currentText.isEmpty()) {
                    binding.userInfoTextView.text = getString(R.string.empty_string)
                    binding.addUserButton.visibility = View.GONE
                } else {
                    searchUser(currentText)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.addUserButton.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            val userIds = users.map { it.id }
            intent.putStringArrayListExtra(getString(R.string.existing_ids_key), ArrayList(userIds))
            resultLauncher.launch(intent)
        }
    }

    private fun searchUser(currentText: String) {
        val foundUser = users.find { user ->
            user.id.contains(currentText, true) ||
                    user.firstName.contains(currentText, true) ||
                    user.lastName.contains(currentText, true) ||
                    user.address.contains(currentText, true) ||
                    user.email.contains(currentText, true) ||
                    user.birthday.toString().contains(currentText, true) ||
                    formatBirthday(user.birthday).contains(currentText, true) ||
                    user.desc?.contains(currentText, true) == true
        }

        if (foundUser != null) with(binding) {
            userInfoTextView.text = getString(
                R.string.user_info_details,
                foundUser.id,
                foundUser.firstName,
                foundUser.lastName,
                formatBirthday(foundUser.birthday),
                foundUser.address,
                foundUser.email
            )
            addUserButton.visibility = View.GONE
        } else with(binding) {
            userInfoTextView.text = getString(R.string.user_not_found)
            addUserButton.visibility = View.VISIBLE
        }
    }

    private fun registerActivityResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val newUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data?.getParcelableExtra(getString(R.string.user_key), User::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        data?.getParcelableExtra<User>(getString(R.string.user_key))
                    }

                    if (newUser != null) {
                        users.add(newUser)
                        binding.searchField.setText(newUser.id)
                        binding.userInfoTextView.text = getString(
                            R.string.user_info_details,
                            newUser.id,
                            newUser.firstName,
                            newUser.lastName,
                            formatBirthday(newUser.birthday),
                            newUser.address,
                            newUser.email
                        )
                        binding.addUserButton.visibility = View.GONE
                    }
                }
            }
    }

    private fun formatBirthday(birthday: Long): String {
        val birthdayStr = birthday.toString()

        if (birthdayStr.length > 8) {
            return try {
                val date = Date(birthday)
                val formatter = SimpleDateFormat(getString(R.string.birthday_format), Locale.getDefault())
                formatter.format(date)
            } catch (_: Exception) {
                birthdayStr
            }
        } else {
            val dateToParse = birthdayStr.padStart(8, '0')
            return try {
                val parser = SimpleDateFormat(getString(R.string.date_parse_format), Locale.getDefault())
                parser.isLenient = false
                val date = parser.parse(dateToParse)
                date?.let {
                    val formatter = SimpleDateFormat(getString(R.string.birthday_format), Locale.getDefault())
                    formatter.format(it)
                } ?: birthdayStr
            } catch (_: Exception) {
                birthdayStr
            }
        }
    }
}
