package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.databinding.FragmentCalculatorBinding
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
                    input.text
                }
            }
        }
        return binding.root
    }
}