package com.example.javBridge.viewModel

import androidx.lifecycle.*
import com.example.javBridge.database.Movie
import com.example.javBridge.database.BridgeRepository

class MainViewModel(
    private val bridgeRepository: BridgeRepository,
) : ViewModel() {
    val liveFilter = MutableLiveData<List<Movie>>()
    fun flowMovie(id: String) = bridgeRepository.flowMovie(id)
    suspend fun susAdd(movie: Movie) = bridgeRepository.addMovie(movie)

    fun liveAllMovies() = bridgeRepository.flowAllMovies().asLiveData()
    fun flowAllMovies() = bridgeRepository.flowAllMovies()
    fun liveUrls() = bridgeRepository.liveUrls()
    fun filter(start: String, end: String, actress: String?, studio: String?) =
        bridgeRepository.filterMovies(start, end, actress, studio)
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