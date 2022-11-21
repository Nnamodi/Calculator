package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.databinding.SeparatorItemBinding
import com.roland.android.calculator.util.Constants.PATTERN
import java.text.SimpleDateFormat
import java.util.*

class SeparatorViewHolder(private val binding: SeparatorItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(date: Date) {
        val pastDate: (days: Int) -> Date = {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, it)
            calendar.time
        }
        (when {
            format(date) == format(Calendar.getInstance().time) -> { "Today" }
            format(date) == format(pastDate(-1)) -> { "Yesterday" }
            format(date) == format(pastDate(-2)) -> { "2 days ago" }
            format(date) == format(pastDate(-3)) -> { "3 days ago" }
            else -> { format(date) }
        }).also {
            binding.historyDate.text = it
        }
    }

    private fun format(date: Date): String = SimpleDateFormat(
        PATTERN,
        Locale.getDefault()
    ).format(date)

    companion object {
        fun create(parent: ViewGroup) = SeparatorViewHolder(
            SeparatorItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}