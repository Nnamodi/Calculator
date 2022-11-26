package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.databinding.ItemHistoryBinding

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
            if (eq.error) {
                inputErrorContainer.visibility = View.VISIBLE
                errorMessageContainer.visibility = View.VISIBLE
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