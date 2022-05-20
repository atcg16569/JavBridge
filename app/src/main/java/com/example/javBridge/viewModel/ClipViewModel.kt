package com.example.javBridge.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.javBridge.database.BridgeRepository
import com.example.javBridge.database.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipViewModel(private val repository: BridgeRepository) : ViewModel() {
    fun flowMovie(id: String) = repository.flowMovie(id)
    fun addMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        repository.addMovie(movie)
    }
}

class ClipViewModelFactory(private val repository: BridgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}