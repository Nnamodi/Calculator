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
        binding.historyDate.text = SimpleDateFormat(
            PATTERN,
            Locale.getDefault()
        ).format(date)
    }

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