package com.example.javBridge.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.javBridge.database.BridgeRepository
import com.example.javBridge.database.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UrlViewModel(private val repository: BridgeRepository) : ViewModel() {
    fun liveUrls() = repository.liveUrls()
    fun liveUrl(name: String) = repository.liveUrl(name)
    fun add(url: Url) = viewModelScope.launch(Dispatchers.IO) {
        repository.addUrl(url)
    }

    fun remove(url: Url) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeUrl(url)
    }

    fun update(url: Url) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateUrl(url)
    }
}

class UrlViewModelFactory(private val repository: BridgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UrlViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UrlViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}