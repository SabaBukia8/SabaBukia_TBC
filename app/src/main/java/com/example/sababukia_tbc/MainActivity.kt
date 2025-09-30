package com.example.sababukia_tbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    lateinit var numberET: EditText
    lateinit var spellOutButton: Button
    lateinit var result: TextView
     lateinit var languageSwitch: SwitchMaterial

     private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {





        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        initializeViews()
        setupClickListeners()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        numberET = findViewById(R.id.numberET)
        spellOutButton = findViewById(R.id.spellOutButton)
        result = findViewById(R.id.result)
        languageSwitch = findViewById(R.id.languageSwitch)
    }

    private fun setupClickListeners() {
        spellOutButton.setOnClickListener {
            convertNumberToWords()
        }
    }
    private fun convertNumberToWords() {

        val inputText = numberET.text.toString().trim()

        if (inputText.isEmpty()) {
            showError("Please enter a number")
            return
        }

        try {
            val number = inputText.toInt()

            if (number < 1 || number > 1000) {
                showError("Please enter a number between 1 and 1000")
                return
            }

            val words = if (languageSwitch.isChecked) {
                spellOutNumberEnglish(number)
            } else {
                spellOutNumberGeorgian(number)
            }
            showResult(words)
        } catch (e: NumberFormatException) {
            showError("Please enter a valid number")
        }
    }

    private fun spellOutNumberEnglish(number: Int): String {
        if (number == 1000) return "One Thousand"

        val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine")
        val teens = arrayOf("Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        return when {
            number >= 100 -> {
                val hundreds = number / 100
                val remainder = number % 100
                val hundredText = "${units[hundreds]} Hundred"

                if (remainder == 0) hundredText else "$hundredText ${spellOutNumberEnglish(remainder)}"
            }
            number >= 20 -> {
                val tensDigit = number / 10
                val unitsDigit = number % 10
                if (unitsDigit == 0) tens[tensDigit] else "${tens[tensDigit]}-${units[unitsDigit]}"
            }
            number >= 10 -> teens[number - 10]
            else -> units[number]
        }
    }

    private fun spellOutNumberGeorgian(number: Int): String {
        if (number == 1000) return "ათასი"

        val units = arrayOf("", "ერთი", "ორი", "სამი", "ოთხი", "ხუთი", "ექვსი", "შვიდი", "რვა", "ცხრა")
        val teens = arrayOf("ათი", "თერთმეტი", "თორმეტი", "ცამეტი", "თოთხმეტი", "თხუთმეტი", "თექვსმეტი", "ჩვიდმეტი", "თვრამეტი", "ცხრამეტი")
        val tens = arrayOf("", "", "ოცი", "ოცდაათი", "ორმოცი", "ორმოცდაათი", "სამოცი", "სამოცდაათი", "ოთხმოცი", "ოთხმოცდაათი")

        return when {
            number >= 100 -> {
                val hundreds = number / 100
                val remainder = number % 100
                val hundredText = when (hundreds) {
                    1 -> "ასი"
                    2 -> "ორასი"
                    3 -> "სამასი"
                    4 -> "ოთხასი"
                    5 -> "ხუთასი"
                    6 -> "ექვსასი"
                    7 -> "შვიდასი"
                    8 -> "რვაასი"
                    9 -> "ცხრაასი"
                    else -> ""
                }

                if (remainder == 0) hundredText else {
                    val remainderText = spellOutNumberGeorgian(remainder)
                    hundredText.replace("ასი", "ას") + remainderText
                }
            }
            number >= 20 -> {
                val tensDigit = number / 10
                val unitsDigit = number % 10
                if (unitsDigit == 0) {
                    tens[tensDigit]
                } else {
                    val baseTwenty = when (tensDigit % 2) {
                        0 -> {tens[tensDigit].replace("ი", "")
                        }
                        else -> {tens[tensDigit].replace("ათი", "")
                        }
                    }

                        "$baseTwenty${if (tensDigit % 2 == 0) "და"+"${units[unitsDigit]}" else "${teens[unitsDigit]}"}"


                }
            }
            number >= 10 -> teens[number - 10]
            else -> units[number]
        }
    }
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        result.text = "Error: $message"
        result.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
    }
    private fun showResult(words: String) {
        result.text = words
        result.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
    }
}