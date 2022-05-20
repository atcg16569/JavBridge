package com.example.javBridge.viewModel

import androidx.lifecycle.*
import com.example.javBridge.database.Movie
import com.example.javBridge.database.BridgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val bridgeRepository: BridgeRepository,
) : ViewModel() {
    val liveFilter = MutableLiveData<List<Movie>>()
    fun flowMovie(id: String) = bridgeRepository.flowMovie(id)
    suspend fun susAdd(movie: Movie) = bridgeRepository.addMovie(movie)
    fun removeMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        bridgeRepository.removeMovie(movie)
    }
    fun liveAllMovies() = bridgeRepository.flowAllMovies().asLiveData()
    fun flowAllMovies() = bridgeRepository.flowAllMovies()
    fun liveUrls() = bridgeRepository.liveUrls()
    fun moviesByDate(start: String, end: String) = bridgeRepository.moviesByDate(start, end)
    fun moviesByActress(actress: String) = bridgeRepository.moviesByActress(actress)
    fun moviesByStudio(studio: String) = bridgeRepository.moviesByStudio(studio)
}

class MainViewModelFactory(
    private val bridgeRepository: BridgeRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(bridgeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}