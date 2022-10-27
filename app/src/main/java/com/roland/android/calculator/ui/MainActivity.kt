package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.roland.android.calculator.databinding.ActivityMainBinding
import com.roland.android.calculator.util.Constants.NUM_SCREENS
import com.roland.android.calculator.util.Preference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.i("MenuItemStuff", "MainActivity - OnCreate")
        setContentView(binding.root)
        setTheme()

        binding.viewPager.adapter = PagerAdapter(this)
    }

    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_SCREENS

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) { CalculatorFragment() }
            else { HistoryFragment() }
        }
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