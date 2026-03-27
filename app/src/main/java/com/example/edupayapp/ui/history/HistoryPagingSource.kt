package com.example.edupayapp.ui.history

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.PaymentHistoryItem

class HistoryPagingSource(
    private val authService: AuthService,
    private val parentId: String,
    private val statusFilter: String
) : PagingSource<Int, PaymentHistoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaymentHistoryItem> {
        return try {
            // Start at page 1 if no key is present
            val currentPage = params.key ?: 1

            // Hit the Ktor endpoint just added to AuthService
            val response = authService.getPaymentHistory(
                parentId = parentId,
                page = currentPage,
                statusFilter = statusFilter
            )

            LoadResult.Page(
                data = response.results,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.next != null) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PaymentHistoryItem>): Int? {
        // Tells the library how to refresh the data without losing the user's scroll position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}