package com.sahih.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahih.data.NewsItem
import com.sahih.data.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val items: List<NewsItem>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class NewsViewModel(private val repo: NewsRepository = NewsRepository()) : ViewModel() {
    private val _state = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val state: StateFlow<NewsUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = NewsUiState.Loading
            _state.value = try {
                NewsUiState.Success(repo.getScamNews())
            } catch (e: Exception) {
                NewsUiState.Error(e.message ?: "Couldn't load news")
            }
        }
    }
}