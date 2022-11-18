package com.roland.android.calculator.ui.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.roland.android.calculator.R
import com.roland.android.calculator.viewmodel.UiModel

class HistoryAdapter(private val onClick: (String) -> Unit) :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(DIFF_UTIL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("ViewHolderStuff", "onCreateViewHolder: $viewType")
        return if (viewType == R.layout.item_history) {
            HistoryViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.HistoryItem -> (holder as HistoryViewHolder).bind(uiModel.history, onClick)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.date)
                null -> throw UnsupportedOperationException("Unknown holder")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.HistoryItem -> R.layout.item_history
            is UiModel.SeparatorItem -> R.layout.separator_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.HistoryItem && newItem is UiModel.HistoryItem &&
                        oldItem.history == newItem.history) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.date == newItem.date)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}