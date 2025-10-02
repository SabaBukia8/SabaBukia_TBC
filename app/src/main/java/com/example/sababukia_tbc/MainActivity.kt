package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding

data class User(val fullName: String, val email: String)
class MainActivity : AppCompatActivity() {
    lateinit var enterName : AppCompatEditText
    lateinit var enterEmail : AppCompatEditText
    lateinit var addUserBtn : AppCompatButton
    lateinit var userCount : AppCompatTextView
    lateinit var checkEmail : AppCompatEditText
    lateinit var getUserInfoBtn : AppCompatButton
    lateinit var userInfo : AppCompatTextView

    private val users = mutableListOf<User>()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        initializeViews()
        clickListener()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initializeViews(){
         enterName = binding.enterName
         enterEmail = binding.enterEmail
         addUserBtn = binding.addUserBtn
         userCount = binding.userCount
         checkEmail = binding.checkEmail
         getUserInfoBtn = binding.getUserInfoBtn
         userInfo = binding.userInfo
    }
    private fun clickListener(){
        addUserBtn.setOnClickListener {
            addUser()
        }
        getUserInfoBtn.setOnClickListener {
            checkUser()
        }
    }
    private fun addUser(){
        val fullName = enterName.text.toString().trim()
        val email = enterEmail.text.toString().trim()
        if (fullName.isEmpty()){
            enterName.error = "Please enter your full name"
            return
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmail.error = "Please enter a valid email address"
            return
        }

        val emailExists = users.find { it.email.equals(email, ignoreCase = true) }
        if (emailExists != null) {
            enterEmail.error = "There already is a user with this email!"
            return
        }

        val user = User(fullName, email)
            users.add(user)

        userCount.text = "Users -> ${users.size}"

        enterName.text?.clear()
        enterEmail.text?.clear()
    }

    private fun checkUser(){

        val email = checkEmail.text.toString().trim()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            checkEmail.error = "Please enter a valid email address"
            return
        }
        if (!userExists(email)){
            userInfo.text = "User not found"
        } else{
            val activeUser = userByEmail(email)
            userInfo.text = "Name = ${activeUser?.fullName}\n Email = ${activeUser?.email}"
        }
    }
    fun userExists(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }
    fun userByEmail(email: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }
}