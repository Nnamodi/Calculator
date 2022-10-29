package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roland.android.calculator.R
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

        // overflow menu
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_calculator, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.change_theme -> { themeDialog(); true }
                    else -> false
                }
            }
        })
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem == 0) { super.onBackPressed() }
        else { binding.viewPager.currentItem =- 1 }
    }

    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_SCREENS

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) { CalculatorFragment() }
            else { HistoryFragment() }
        }
    }

    private fun themeDialog() {
        val options = resources.getStringArray(R.array.dialog_options)
        val checkedOption = Preference.getTheme(this)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.dialog_close)) { _, _ -> }
            .setSingleChoiceItems(options, checkedOption) { dialog, option ->
                val mode = when (option) {
                    0 -> { AppCompatDelegate.MODE_NIGHT_YES }
                    1 -> { AppCompatDelegate.MODE_NIGHT_NO }
                    else -> { AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM }
                }
                AppCompatDelegate.setDefaultNightMode(mode)
                Preference.setTheme(this, option)
                dialog.dismiss()
            }.show()
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