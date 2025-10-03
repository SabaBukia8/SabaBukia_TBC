package com.example.sababukia_tbc

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var enterAnagram : AppCompatEditText
    lateinit var saveBtn : AppCompatButton
    lateinit var outputBtn : AppCompatButton
    lateinit var anagrams : AppCompatTextView
    lateinit var clearBtn : AppCompatButton

    private val anagramList = mutableListOf<String>()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        enterAnagram = binding.enterAnagram
        saveBtn = binding.saveBtn
        outputBtn = binding.outputBtn
        anagrams = binding.anagrams
        clearBtn = binding.clearBtn

        saveBtn.text = getString(R.string.save_button)
        outputBtn.text = getString(R.string.output_button)
        clearBtn.text = getString(R.string.clear_button)
        enterAnagram.hint = getString(R.string.enter_anagram_hint)

        setupClickListeners()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.saveBtn.setOnClickListener {
            saveAnagram()
        }

        binding.outputBtn.setOnClickListener {
            groupAnagrams()
        }

        binding.clearBtn.setOnClickListener {
            clearThePage()
        }
    }

    private fun saveAnagram(){
        val input = binding.enterAnagram.text.toString().trim()

        if (input.isEmpty()) {
            enterAnagram.error = getString(R.string.error_empty_anagram)
            return
        }

        val checkForCopy = anagramList.any { it.equals(input, ignoreCase = true) }

        if (checkForCopy) {
            enterAnagram.error = getString(R.string.error_duplicate_word, input)
            Toast.makeText(this, getString(R.string.toast_word_exists, input), Toast.LENGTH_SHORT).show()
            return
        } else{
            anagramList.add(input)
            binding.enterAnagram.text?.clear()
            enterAnagram.error = null
            Toast.makeText(this, getString(R.string.success_word_added, input), Toast.LENGTH_SHORT).show()
        }
    }


    private fun groupAnagrams(){
        if (anagramList.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_words_saved, Toast.LENGTH_SHORT).show()
            return
        }

        val anagramGroups = groupingAnagrams(anagramList)

        binding.displayAnagrams.removeAllViews()

        anagramGroups.forEach { group ->
            if (group.size > 1) {
                addAnagrams(group)
            }
        }

        val groupCount = anagramGroups.count { it.size > 1 }
        binding.anagrams.text = getString(R.string.anagram_groups_count, groupCount)

        if (groupCount == 0) {
            Toast.makeText(this, R.string.toast_no_anagram_groups, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAnagrams(groupOfAnagrams: List<String>) {
        val groupTextView = AppCompatTextView(this).apply {
            text = "[${groupOfAnagrams.joinToString(", ")}]"

        }
        binding.displayAnagrams.addView(groupTextView)
    }

    private fun groupingAnagrams(words: List<String>): List<List<String>> {
        val wordsInAnagram = words.toMutableList()
        val result = mutableListOf<List<String>>()

        while (wordsInAnagram.isNotEmpty()) {
            val targetWord = wordsInAnagram.removeAt(0)
            val thisGroup = mutableListOf(targetWord)

            var i = 0
            while (i < wordsInAnagram.size) {
                if (isAnAnagram(targetWord, wordsInAnagram[i])) {
                    thisGroup.add(wordsInAnagram.removeAt(i))
                } else {
                    i++
                }
            }

            result.add(thisGroup)
        }

        return result
    }

    private fun isAnAnagram(word1: String, word2: String): Boolean {
        if (word1.length != word2.length) return false

        val sorted1 = word1.lowercase().toCharArray().sorted().joinToString("")
        val sorted2 = word2.lowercase().toCharArray().sorted().joinToString("")

        return sorted1 == sorted2
    }

    private fun clearThePage(){
        anagramList.clear()
        binding.displayAnagrams.removeAllViews()
        binding.anagrams.text = getString(R.string.anagram_groups_count, 0)
    }
}