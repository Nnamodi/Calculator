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
import com.roland.android.calculator.data.TrigFunctions
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
            val buttons = setOf(button00, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
            buttons.forEach {
                val digit = it.text.toString()
                it.setOnClickListener { calcViewModel.onAction(CalculatorActions.Numbers(digit)) }
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
            // pi
            pi?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Pi)
            }
            // log
            log?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Log)
            }
            // trigonometric input
            sin?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Trigonometry(TrigFunctions.Sine))
            }
            cos?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Trigonometry(TrigFunctions.Cosine))
            }
            tan?.setOnClickListener {
                calcViewModel.onAction(CalculatorActions.Trigonometry(TrigFunctions.Tangent))
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

            // expand hidden buttons
            expandButton?.setOnCheckedChangeListener { _, checked ->
                // giving binding-layout variable a value to toggle visibility
                expand = checked
            }
        }
        lifecycleScope.launchWhenStarted {
            calcViewModel.stateFlow.collectLatest {
                binding.apply {
                    input.setText(it.input)
                    input.setSelection(it.input.length)
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