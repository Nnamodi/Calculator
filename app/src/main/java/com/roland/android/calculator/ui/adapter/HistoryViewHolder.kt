package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.databinding.ItemHistoryBinding
import com.roland.android.calculator.util.Regex.accessibilityRegex

class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(eq: Equation, onClick: (String) -> Unit) {
        binding.apply {
            degRad.text = eq.degRad
            equation.apply {
                setOnClickListener { onClick(eq.input) }
                text = eq.input
            }
            eqResult.apply {
                setOnClickListener { onClick(eq.result) }
                text = eq.result
            }
            inputError.apply {
                setOnClickListener { onClick(eq.input) }
                text = eq.input
            }
            errorMessage.text = eq.errorMessage
            // giving binding-variable a value
            inputIsError = eq.error
            if (!eq.error) {
                inputErrorContainer.visibility = View.GONE
                errorMessageContainer.visibility = View.GONE
            }
            // make history accessible
            val string: (Int) -> String = { root.context.getString(it) }
            setOf(equation, eqResult, inputError).forEach {
                it.contentDescription = it.text.toString().accessibilityRegex(string)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup) = HistoryViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}