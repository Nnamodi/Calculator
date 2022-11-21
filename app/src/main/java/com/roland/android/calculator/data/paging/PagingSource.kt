package com.roland.android.calculator.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDao
import com.roland.android.calculator.util.Constants.STARTING_PAGE
import kotlinx.coroutines.delay

class PagingSource(private val source: EquationDao) : PagingSource<Int, Equation>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Equation> {
        val currentPage = params.key ?: STARTING_PAGE

        return try {
            val entities = source.getEquations()

            // simulate page loading
            if (currentPage != 0) delay(1000)

            LoadResult.Page(
                data = entities,
                prevKey = if (currentPage == 0) null else currentPage - 1, // possible cause for bug
                nextKey = if (currentPage == 0) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Equation>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}