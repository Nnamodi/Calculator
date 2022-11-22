package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.SeparatorItemBinding
import com.roland.android.calculator.util.Constants.PATTERN
import java.text.SimpleDateFormat
import java.util.*

class SeparatorViewHolder(private val binding: SeparatorItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(date: Date) {
        val string: (Int, Int) -> String = { string, days ->
            if (days > 2 || days != 7) { binding.root.context.getString(string, days) }
            else { binding.root.context.getString(string) }
        }
        val pastDate: (days: Int) -> Date = {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, it)
            calendar.time
        }
        when (format(date)) {
            format(Calendar.getInstance().time) -> { string(R.string.today, 0) }
            format(pastDate(-1)) -> { string(R.string.yesterday, 1) }
            format(pastDate(-2)) -> { string(R.string.days_ago, 2) }
            format(pastDate(-3)) -> { string(R.string.days_ago, 3) }
            format(pastDate(-4)) -> { string(R.string.days_ago, 4) }
            format(pastDate(-5)) -> { string(R.string.days_ago, 5) }
            format(pastDate(-6)) -> { string(R.string.days_ago, 6) }
            format(pastDate(-7)) -> { string(R.string.a_week_ago, 7) }
            else -> { format(date) }
        }.also { day ->
            binding.apply {
                historyDate.text = day
                root.apply {
                    setOnClickListener { historyDate.text =
                        if (historyDate.text == day) { format(date) } else { day }
                    }
                    setOnLongClickListener { true }
                }
            }
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