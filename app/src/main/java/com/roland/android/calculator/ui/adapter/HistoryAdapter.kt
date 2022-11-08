package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.databinding.ItemHistoryBinding

class HistoryAdapter : ListAdapter<Equation, HistoryAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(eq: Equation) {
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
            }
        }

        private fun onClick(text: CharSequence) {}
    }

    class DiffCallback: DiffUtil.ItemCallback<Equation>() {
        override fun areItemsTheSame(oldItem: Equation, newItem: Equation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Equation, newItem: Equation): Boolean {
            return oldItem == newItem
        }
    }
}