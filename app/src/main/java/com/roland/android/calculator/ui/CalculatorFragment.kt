package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COS_INV
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.EULER
import com.roland.android.calculator.util.Constants.INV_LOG
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.MINUS
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.PI
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.ROOT
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SIN_INV
import com.roland.android.calculator.util.Constants.SQUARE
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TAN_INV
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Preference.getTheme
import com.roland.android.calculator.util.Preference.setTheme
import com.roland.android.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

class CalculatorFragment : Fragment() {
    private val calcViewModel: CalculatorViewModel by viewModels()
    private lateinit var binding: FragmentCalculatorBinding
    private val _inverse = MutableStateFlow(false)
    private val inverse = _inverse.asStateFlow()
    private lateinit var squared: String
    private lateinit var sinInverse: CharSequence
    private lateinit var cosInverse: String
    private lateinit var tanInverse: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalculatorBinding.inflate(layoutInflater)
        squared = getString(R.string.squared)
        sinInverse = getString(R.string.arc_sine)
        cosInverse = getString(R.string.arc_cosine)
        tanInverse = getString(R.string.arc_tangent)
        binding.apply {
            // disable keyboard for editText
            input.showSoftInputOnFocus = false
            // enter digits
            setOf(button00, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
                .forEach {
                val digit = it?.text.toString()
                it?.setOnClickListener { calcViewModel.onAction(CalculatorActions.Numbers(digit)) }
            }
            // operators
            setOf(add, subtract, divide, multiply, modulus, euler, bracket).forEach {
                val symbol = it.text.toString()
                it.setOnClickListener {
                    val action = when (symbol) {
                        ADD -> CalculatorActions.Operators(CalculatorOperations.Add)
                        MINUS -> CalculatorActions.Operators(CalculatorOperations.Subtract)
                        DIVIDE -> CalculatorActions.Operators(CalculatorOperations.Divide)
                        MULTIPLY -> CalculatorActions.Operators(CalculatorOperations.Multiply)
                        MOD -> CalculatorActions.Operators(CalculatorOperations.Modulus)
                        EULER -> CalculatorActions.Euler
                        else -> CalculatorActions.Bracket // "( )"
                    }
                    calcViewModel.onAction(action)
                }
            }
            // trigonometric input
            setOf(sin, cos, tan).forEach { button ->
                button.setOnClickListener {
                    val trigFunction = when (button.text) {
                        "sin" -> TrigFunctions.Sine
                        "cos" -> TrigFunctions.Cosine
                        "tan" -> TrigFunctions.Tangent
                        sinInverse -> TrigFunctions.ASine
                        cosInverse -> TrigFunctions.ACosine
                        else -> TrigFunctions.ATangent // "tanInverse"
                    }
                    calcViewModel.onAction(CalculatorActions.Trigonometry(trigFunction))
                }
            }
            // other buttons
            setOf(square, squareRoot, pi, log, buttonAc, buttonDel, equals, decimal).forEach {
                val input = it.text.toString()
                it.setOnClickListener {
                    val action = when (input) {
                        "^" -> CalculatorActions.Square
                        "x²" -> CalculatorActions.SquareInv
                        "√" -> CalculatorActions.SquareRoot
                        PI -> CalculatorActions.Pi
                        "log" -> CalculatorActions.Log
                        INV_LOG -> CalculatorActions.LogInv
                        "AC" -> CalculatorActions.Clear
                        "Del" -> CalculatorActions.Delete
                        "=" -> CalculatorActions.Calculate
                        else -> CalculatorActions.Decimal // "·"
                    }
                    calcViewModel.onAction(action)
                }
            }
            degRad.setOnClickListener { degRadConfig(clicked = true) }
            inv.setOnClickListener { _inverse.value = !inverse.value }
            // expand hidden buttons
            expandButton?.setOnCheckedChangeListener { _, checked ->
                _inverse.value = false
                // giving binding-layout variable a value to toggle visibility
                expand = checked
            }
        }
        lifecycleScope.launchWhenStarted {
            calcViewModel.stateFlow.collectLatest {
                binding.apply {
                    input.setText(it.input)
                    input.setSelection(it.input.length)
                    result?.text = it.result
                    // giving binding-layout variable a value
                    error = it.error
                    degRadConfig()
                }
            }
        }
        lifecycleScope.launchWhenResumed {
            inverse.collectLatest {
                binding.apply {
                    if (it) {
                        log.text = INV_LOG
                        sin.text = sinInverse
                        cos.text = cosInverse
                        tan.text = tanInverse
                        squareRoot.text = squared
                    } else {
                        sin.text = getString(R.string.sine)
                        cos.text = getString(R.string.cosine)
                        tan.text = getString(R.string.tangent)
                        log.text = getString(R.string.logarithm)
                        squareRoot.text = getString(R.string.square_root)
                    }
                }
            }
        }
        setupMenuItem()
        return binding.root
    }

    private fun degRadConfig(clicked: Boolean = false) {
        // perform click action
        if (clicked) { calcViewModel.onAction(CalculatorActions.DegRad) }

        val input = calcViewModel.stateFlow.value.input
        val trigFunctions = setOf(SIN, COS, TAN, SIN_INV, COS_INV, TAN_INV)
        val degRad = Preference.getDegRad(requireContext())
        // giving binding-layout variable a value
        binding.degRadValue = if (degRad == RAD) { DEG } else { RAD }

        (activity as AppCompatActivity).supportActionBar?.title = if (trigFunctions.any { trig ->
                input.contains(trig) }
        ) { degRad } else { "" }
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