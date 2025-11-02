package com.example.edupayapp.ui.help

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class HelpTab {
    FAQs,
    ContactUs
}

data class HelpUiState(
    val selectedTab: HelpTab = HelpTab.FAQs
)

class HelpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState = _uiState.asStateFlow()

    fun onTabSelected(tab: HelpTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}