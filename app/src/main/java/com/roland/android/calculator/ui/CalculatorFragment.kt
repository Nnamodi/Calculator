package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
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
import com.roland.android.calculator.util.Constants.EULER_INV
import com.roland.android.calculator.util.Constants.FACT
import com.roland.android.calculator.util.Constants.INV_LOG
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.LOG_N
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
import com.roland.android.calculator.util.Haptic
import com.roland.android.calculator.util.Haptic.haptic
import com.roland.android.calculator.util.Preference
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
    private var toolbarSet = false
    private lateinit var squared: String
//    private lateinit var cubeRoot: String
    private lateinit var sinInverse: String
    private lateinit var cosInverse: String
    private lateinit var tanInverse: String
    private lateinit var eulerInverse: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        addObservers(); toolbarSet = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        squared = getString(R.string.squared)
//        cubeRoot = getString(R.string.cube_root)
        sinInverse = getString(R.string.arc_sine)
        cosInverse = getString(R.string.arc_cosine)
        tanInverse = getString(R.string.arc_tangent)
        eulerInverse = getString(R.string.euler_inv)
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
                        INV_LOG -> CalculatorActions.LogInv
                        "ln" -> CalculatorActions.LogN
                        EULER_INV -> CalculatorActions.EulerInv
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
            inv.setOnClickListener { _inverse.value = !inverse.value }
            // expand hidden buttons
            expandButton?.setOnCheckedChangeListener { _, checked ->
                _inverse.value = false
                // giving binding-layout variable a value to toggle visibility
                expand = checked
            }
            setOf(degRad, inv, expandButton)
                .forEach { it?.setOnTouchListener(Haptic.ClickFeedback(requireContext())) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!toolbarSet) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        lifecycleScope.launchWhenResumed {
            calcViewModel.stateFlow.collectLatest {
                binding.apply {
                    input.setText(it.input)
                    input.setSelection(it.input.length)
                    input.isCursorVisible = !calcViewModel.inputIsAnswer
                    result?.text = it.result
                    errorText.text = it.errorMessage
                    // giving binding-layout variable a value
                    error = it.error
                    delete = delButtonText(it.input)

                    wrongInput?.setText(it.input)
                    wrongInput?.setSelection(it.input.length)
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
                }
            }
        }
    }

    private fun addObservers() {
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) { toolbarSet = false }
            if (event == Lifecycle.Event.ON_DESTROY) { _binding = null }
        })
    }

    private fun delButtonText(input: String): Boolean {
        return when {
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
        val degRad = Preference.getDegRad(requireContext())
        // giving binding-layout variable a value
        binding.degRadValue = if (degRad == RAD) { DEG } else { RAD }

        (activity as AppCompatActivity).supportActionBar?.title = if (trigFunctions.any { trig ->
                input.contains(trig) }
        ) { degRad } else { "" }
    }
}