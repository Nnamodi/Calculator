package com.roland.android.calculator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.ActivityMainBinding
import com.roland.android.calculator.util.Preference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setTheme()

        val host = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = host.navController
        setupActionBarWithNavController(navController)
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