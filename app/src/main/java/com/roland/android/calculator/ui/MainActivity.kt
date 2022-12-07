package com.roland.android.calculator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.ActivityMainBinding
import com.roland.android.calculator.util.Preference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Calculator)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme()
    }

    override fun onSupportNavigateUp(): Boolean {
        findNavController(R.id.fragmentContainerView).navigateUp()
        return true
    }

    private fun setTheme() {
        val mode = when (Preference.getTheme(this)) {
            0 -> { AppCompatDelegate.MODE_NIGHT_YES }
            1 -> { AppCompatDelegate.MODE_NIGHT_NO }
            else -> { AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM }
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}