package com.roland.android.calculator.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDao
import com.roland.android.calculator.data.paging.PagingSource
import com.roland.android.calculator.util.Constants.LOAD_PAGE_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class EquationRepository(
    private val equationDao: EquationDao,
    private val viewModelScope: CoroutineScope
) {
    fun getEquations(): Flow<PagingData<Equation>> {
        return Pager(
            config = PagingConfig(
                pageSize = LOAD_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PagingSource(
                equationDao
            ) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun addCalculation(equation: Equation) {
        equationDao.addCalculation(equation)
    }

    suspend fun clearHistory() {
        equationDao.clearHistory()
    }
}