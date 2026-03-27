package com.example.edupayapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.PaymentHistoryItem
import com.example.edupayapp.data.tokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class HistoryViewModel : ViewModel() {
    private val authService = AuthService()
    // Grabs the logged-in parent's UUID from your token storage
    private val parentId = tokenStorage.getUserId() ?: ""

    // Tracks what the user types in the search bar
    val searchQuery = MutableStateFlow("")

    // The Engine: Automatically re-fetches from the backend if the search query changes
    val pagedTransactions: Flow<PagingData<PaymentHistoryItem>> = searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { HistoryPagingSource(authService, parentId, query) }
        ).flow
    }.cachedIn(viewModelScope) // Caches the data so it survives screen rotations

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}