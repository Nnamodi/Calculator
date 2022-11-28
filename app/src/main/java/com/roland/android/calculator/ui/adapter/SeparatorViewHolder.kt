package com.roland.android.calculator.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.SeparatorItemBinding
import com.roland.android.calculator.util.Constants.FULL_PATTERN
import com.roland.android.calculator.util.Constants.PATTERN
import com.roland.android.calculator.util.Constants.YEAR
import java.text.SimpleDateFormat
import java.util.*

class SeparatorViewHolder(private val binding: SeparatorItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(date: Date) {
        when {
            format(date) == format(Calendar.getInstance().time) -> { string(R.string.today, 0) }
            format(date) == format(pastDate(-1)) -> { string(R.string.yesterday, 1) }
            format(date) == format(pastDate(-2)) -> { string(R.string.days_ago, 2) }
            format(date) == format(pastDate(-3)) -> { string(R.string.days_ago, 3) }
            format(date) == format(pastDate(-4)) -> { string(R.string.days_ago, 4) }
            format(date) == format(pastDate(-5)) -> { string(R.string.days_ago, 5) }
            format(date) == format(pastDate(-6)) -> { string(R.string.days_ago, 6) }
            format(date) == format(pastDate(-7)) -> { string(R.string.a_week_ago, 7) }
            format(date, YEAR) != format(Calendar.getInstance().time, YEAR) -> { format(date, FULL_PATTERN) }
            else -> { format(date) }
        }.also { day ->
            binding.apply {
                historyDate.text = day
                root.apply {
                    setOnClickListener { historyDate.text =
                        when (day) {
                            format(date, FULL_PATTERN) -> historyDate.text
                            historyDate.text -> format(date)
                            else -> day
                        }
                    }
                    setOnLongClickListener { true }
                }
            }
        }
    }

    private val string: (Int, Int) -> String = { string, days ->
        if (days > 2 || days != 7) { binding.root.context.getString(string, days) }
        else { binding.root.context.getString(string) }
    }

    private val pastDate: (days: Int) -> Date = {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, it)
        calendar.time
    }

    private fun format(date: Date, pattern: String = PATTERN): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(date)

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