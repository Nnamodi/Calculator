package com.roland.android.calculator.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.text.Spanned
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roland.android.calculator.R
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.TrigFunctions
import com.roland.android.calculator.databinding.FragmentCalculatorBinding
import com.roland.android.calculator.util.Accessibility.accessCalculator
import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.COMPUTE_FORMAT
import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COS_INV
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.EULER
import com.roland.android.calculator.util.Constants.FACT
import com.roland.android.calculator.util.Constants.HISTORY
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.LOG_N
import com.roland.android.calculator.util.Constants.MINUS
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.NAVIGATE
import com.roland.android.calculator.util.Constants.PI
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.ROOT
import com.roland.android.calculator.util.Constants.SET_THEME
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SIN_INV
import com.roland.android.calculator.util.Constants.SQUARE
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TAN_INV
import com.roland.android.calculator.util.Constants.THEME
import com.roland.android.calculator.util.Haptic
import com.roland.android.calculator.util.Haptic.haptic
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Preference.getComputeFormat
import com.roland.android.calculator.util.Utility.lifecycleObserver
import com.roland.android.calculator.util.Utility.string
import com.roland.android.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

class CalculatorFragment : Fragment() {
    private val calcViewModel: CalculatorViewModel by viewModels()
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val _inverse = MutableStateFlow(false)
    private val inverse = _inverse.asStateFlow()
    private lateinit var squared: String
//    private lateinit var cubeRoot: String
    private lateinit var sinInverse: Spanned
    private lateinit var cosInverse: Spanned
    private lateinit var tanInverse: Spanned
    private lateinit var logInverse: Spanned
    private lateinit var eulerInverse: Spanned

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        lifecycleObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenuItem(); setupObservables()
        squared = getString(R.string.squared)
//        cubeRoot = getString(R.string.cube_root)
        sinInverse = "sin".string(requireContext())
        cosInverse = "cos".string(requireContext())
        tanInverse = "tan".string(requireContext())
        logInverse = "10".string(requireContext(), R.string.inverse_function)
        eulerInverse = "e".string(requireContext(), R.string.inverse_function)
        binding.apply {
            // disable keyboard for editText
            input.showSoftInputOnFocus = false
            wrongInput?.showSoftInputOnFocus = false
            // enter digits
            setOf(button00, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
                .forEach {
                    val digit = it.text.toString()
                    it.setOnClickListener { calcViewModel.onAction(CalculatorActions.Numbers(digit)) }
                    it.setOnTouchListener(Haptic.ClickFeedback(requireContext()))
                }
            // operators
            setOf(add, subtract, divide, multiply, modulus, euler, factorial, bracket).forEach { button ->
                button.setOnClickListener {
                    val action = when (button.text) {
                        ADD -> CalculatorActions.Operators(CalculatorOperations.Add)
                        MINUS -> CalculatorActions.Operators(CalculatorOperations.Subtract)
                        DIVIDE -> CalculatorActions.Operators(CalculatorOperations.Divide)
                        MULTIPLY -> CalculatorActions.Operators(CalculatorOperations.Multiply)
                        MOD -> CalculatorActions.Operators(CalculatorOperations.Modulus)
                        EULER -> CalculatorActions.Euler
                        "!" -> CalculatorActions.Factorial
                        else -> CalculatorActions.Bracket // "( )"
                    }
                    calcViewModel.onAction(action)
                }
                button.setOnTouchListener(Haptic.ClickFeedback(requireContext()))
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
                button.setOnTouchListener(Haptic.ClickFeedback(requireContext()))
            }
            // other buttons
            setOf(square, squareRoot, pi, log, naturalLog, buttonAc, equals, decimal).forEach { button ->
                button.setOnClickListener {
                    val action = when (button.text) {
                        SQUARE -> CalculatorActions.Square
                        "x²" -> CalculatorActions.SquareInv
                        "√" -> CalculatorActions.SquareRoot
                        PI -> CalculatorActions.Pi
                        "log" -> CalculatorActions.Log
                        logInverse -> CalculatorActions.LogInv
                        "ln" -> CalculatorActions.LogN
                        eulerInverse -> CalculatorActions.EulerInv
                        "AC" -> CalculatorActions.Clear
                        "=" -> CalculatorActions.Calculate
                        else -> CalculatorActions.Decimal // "·"
                    }
                    calcViewModel.onAction(action)
                }
                button.setOnTouchListener(Haptic.ClickFeedback(requireContext()))
            }
            delButton.apply {
                setOnClickListener {
                    calcViewModel.onAction(CalculatorActions.Delete)
                    haptic(requireContext(), 50)
                }
                setOnLongClickListener {
                    calcViewModel.onAction(CalculatorActions.Clear); true
                }
            }
            degRad.setOnClickListener { degRadConfig(clicked = true) }
            toolbarDegRad.setOnClickListener { degRadConfig(clicked = true) }
            inv.setOnClickListener { _inverse.value = !inverse.value }
            // expand hidden buttons
            expandButton?.setOnCheckedChangeListener { _, checked ->
                _inverse.value = false
                // make calculator accessible
                accessCalculator()
                // giving binding-layout variable a value to toggle visibility
                expand = checked
            }
            setOf(degRad, toolbarDegRad, inv, expandButton)
                .forEach { it?.setOnTouchListener(Haptic.ClickFeedback(requireContext())) }
            setOf(input, wrongInput).forEach { input ->
                input?.setOnClickListener {
                    if (input.text.isNotBlank()) { input.isCursorVisible = false }
                }
                input?.let { registerForContextMenu(it) }
            }; result?.let { registerForContextMenu(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            calcViewModel.stateFlow.collectLatest {
                binding.apply {
                    val resultLand = when {
                        it.result.isBlank() -> { it.input }
                        getComputeFormat(requireContext()) != R.id.fraction && '/' in it.result -> { it.input }
                        else -> { it.result }
                    }
                    input.setText(it.input)
                    input.setSelection(it.input.length)
                    input.isCursorVisible = !calcViewModel.inputIsAnswer
                    result?.text = it.result
                    landResult?.text = resultLand
                    errorText.text = it.errorMessage
                    // giving binding-layout variable a value
                    equalled = calcViewModel.equalled
                    error = it.error
                    delete = delButtonText(it.input)

                    wrongInput?.setText(it.input)
                    wrongInput?.setSelection(it.input.length)
                    degRadConfig()
                    // make calculator accessible
                    accessCalculator()
                }
            }
        }
        lifecycleScope.launchWhenResumed {
            inverse.collectLatest {
                binding.apply {
                    if (it) {
                        log.text = logInverse
                        sin.text = sinInverse
                        cos.text = cosInverse
                        tan.text = tanInverse
//                        square.text = cubeRoot
                        squareRoot.text = squared
                        naturalLog.text = eulerInverse
                    } else {
                        sin.text = getString(R.string.sine)
                        cos.text = getString(R.string.cosine)
                        tan.text = getString(R.string.tangent)
                        log.text = getString(R.string.logarithm)
//                        square.text = getString(R.string.square)
                        squareRoot.text = getString(R.string.square_root)
                        naturalLog.text = getString(R.string.natural_log)
                    }
                    // giving binding-variable a value
                    inverseValue = it
                    accessCalculator(inverse = it)
                }
            }
        }
    }

    override fun onDestroy() { super.onDestroy(); _binding = null }

    private fun setupObservables() {
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.calculatorFragment)
        navBackStackEntry.savedStateHandle.apply {
            getLiveData<Boolean>(THEME).observe(viewLifecycleOwner) {
                if (it) { themeDialog() }
            }
            getLiveData<String>(HISTORY).observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) { calcViewModel.onAction(CalculatorActions.EnterEquation(it)) }
            }
            getLiveData<Int>(COMPUTE_FORMAT).observe(viewLifecycleOwner) {
                if (it != 0) { calcViewModel.onAction(CalculatorActions.ComputeFormat) }
            }
            getLiveData<Int>(NAVIGATE).observe(viewLifecycleOwner) { code ->
                val job: (Int, Long) -> Unit = { id, sec ->
                    Handler(getMainLooper()).postDelayed({ findNavController().navigate(id) }, sec)
                }
                when (code) {
                    1 -> { job(R.id.settingsSheet, 100) }
                    2 -> { job(R.id.computeFormatSheet, 100) }
                    3 -> { job(R.id.settingsSheet, 705) }
                    else -> {}
                }
            }
        }
    }

    private fun delButtonText(input: String): Boolean {
        return when {
            calcViewModel.inputIsAnswer -> true
            input.length <= 1 -> false
            input == SIN -> false
            input == COS -> false
            input == TAN -> false
            input == LOG -> false
            input == FACT -> false
            input == ROOT -> false
            input == LOG_N -> false
            input == SIN_INV -> false
            input == COS_INV -> false
            input == TAN_INV -> false
            else -> true
        }
    }

    private fun degRadConfig(clicked: Boolean = false) {
        // perform click action
        if (clicked) { calcViewModel.onAction(CalculatorActions.DegRad) }

        val input = calcViewModel.stateFlow.value.input
        val trigFunctions = setOf(SIN, COS, TAN, SIN_INV, COS_INV, TAN_INV)
        val radDeg = Preference.getDegRad(requireContext())
        // giving binding-layout variable a value
        binding.apply {
            degRad.text = if (radDeg == RAD) { DEG } else { RAD }
            toolbarDegRad.text = radDeg
            toolbarDegRad.visibility = if (trigFunctions.any { trig ->
                    input.contains(trig) }
            ) { View.VISIBLE } else { View.GONE }
        }
        if (clicked) { binding.accessCalculator() }
        (activity as AppCompatActivity).supportActionBar?.title = ""
    }

    private fun setupMenuItem() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_calculator, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.history -> { findNavController().navigate(R.id.move_to_history); true }
                    R.id.settings -> { findNavController().navigate(R.id.settingsSheet); true }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun themeDialog() {
        val options = resources.getStringArray(R.array.dialog_options)
        val checkedOption = Preference.getTheme(requireContext())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.change_theme))
            .setPositiveButton(getString(R.string.dialog_close)) { _, _ -> }
            .setSingleChoiceItems(options, checkedOption) { dialog, option ->
                if (option != checkedOption) {
                    val mode = when (option) {
                        0 -> { AppCompatDelegate.MODE_NIGHT_YES }
                        1 -> { AppCompatDelegate.MODE_NIGHT_NO }
                        else -> { AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM }
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                    Preference.setTheme(requireContext(), option)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(SET_THEME, true)
                    dialog.dismiss()
                }
            }.show()
    }
}