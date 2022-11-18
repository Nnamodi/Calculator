package com.roland.android.calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDatabase
import com.roland.android.calculator.repository.EquationRepository
import com.roland.android.calculator.util.Constants.PATTERN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    // database
    private val repository: EquationRepository
    val getEquation: Flow<PagingData<UiModel>>

    init {
        val equationDao = EquationDatabase.getDatabase(app).equationDao()
        repository = EquationRepository(equationDao, viewModelScope)
        getEquation = repository.getEquations()
            .map { value -> value.map { UiModel.HistoryItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }
                    if (before == null) {
                        return@insertSeparators UiModel.SeparatorItem(
                            after.history.date
                        )
                    }
                    if (before.simpleDate != after.simpleDate) {
                        return@insertSeparators UiModel.SeparatorItem(
                            before.history.date
                        )
                    } else { null }
                }
            }
    }
}

private val UiModel.HistoryItem.simpleDate: String
    get() = SimpleDateFormat(PATTERN, Locale.getDefault())
        .format(this.history.date)

sealed class UiModel {
    data class HistoryItem(val history: Equation): UiModel()
    data class SeparatorItem(val date: Date): UiModel()
}