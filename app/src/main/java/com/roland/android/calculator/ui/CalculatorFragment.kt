package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roland.android.calculator.R
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.databinding.FragmentCalculatorBinding
import com.roland.android.calculator.util.Preference.getTheme
import com.roland.android.calculator.util.Preference.setTheme
import com.roland.android.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.collectLatest

class CalculatorFragment : Fragment() {
    private val calcViewModel: CalculatorViewModel by viewModels()
    private lateinit var binding: FragmentCalculatorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalculatorBinding.inflate(layoutInflater)
        binding.apply {
            // disable keyboard for editText
            input.showSoftInputOnFocus = false
            // enter digits
            button1.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("1"))
            }
            button2.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("2"))
            }
            button3.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("3"))
            }
            button4.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("4"))
            }
            button5.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("5"))
            }
            button6.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("6"))
            }
            button7.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("7"))
            }
            button8.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("8"))
            }
            button9.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("9"))
            }
            button0.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("0"))
            }
            button00.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Numbers("00"))
            }
            // operators
            add.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Operators(CalculatorOperations.Add))
            }
            subtract.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Operators(CalculatorOperations.Subtract))
            }
            divide.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Operators(CalculatorOperations.Divide))
            }
            multiply.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Operators(CalculatorOperations.Multiply))
            }
            modulus?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Operators(CalculatorOperations.Modulus))
            }
            plusMinus?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.PlusMinus)
            }
            bracket?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Bracket)
            }
            // clear input
            buttonAc.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Clear)
            }
            // delete single input
            buttonDel.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Delete)
            }
            // enter decimal
            decimal.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Decimal)
            }
            // calculate input
            equals.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Calculate)
            }
        }
        lifecycleScope.launchWhenStarted {
            calcViewModel.stateFlow.collectLatest {
                val digits = it.digit_1 + (it.operator?.symbol ?: "") + it.digit_2
                binding.apply {
                    input.setText(digits)
                    input.setSelection(digits.length)
                    result.text = it.result
                    // giving binding-layout variable a value
                    error = it.error
                }
            }
        }
        setupMenuItem()
        return binding.root
    }

    private fun setupMenuItem() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_calculator, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.change_theme -> { themeDialog(); true }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun themeDialog() {
        val options = resources.getStringArray(R.array.dialog_options)
        val checkedOption = getTheme(requireContext())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.dialog_close)) { _, _ -> }
            .setSingleChoiceItems(options, checkedOption) { dialog, option ->
                val mode = when (option) {
                    0 -> { AppCompatDelegate.MODE_NIGHT_YES }
                    1 -> { AppCompatDelegate.MODE_NIGHT_NO }
                    else -> { AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM }
                }
                AppCompatDelegate.setDefaultNightMode(mode)
                setTheme(requireContext(), option)
                dialog.dismiss()
            }.show()
    }
}