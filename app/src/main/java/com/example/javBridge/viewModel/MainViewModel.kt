package com.example.javBridge.viewModel

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.javBridge.database.Movie
import com.example.javBridge.database.BridgeRepository
import com.example.javBridge.database.MoviePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val bridgeRepository: BridgeRepository,
) : ViewModel() {
    val liveFilter = MutableLiveData<List<Movie>>()
    suspend fun susAdd(movie: Movie) = bridgeRepository.addMovie(movie)
    fun removeMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        bridgeRepository.removeMovie(movie)
    }

    fun flowAllMovies() = bridgeRepository.flowAllMovies()
    fun liveUrls() = bridgeRepository.liveUrls()
    fun moviesByDate(start: String, end: String) = bridgeRepository.moviesByDate(start, end)
    fun moviesByActress(actress: String) = bridgeRepository.moviesByActress(actress)
    fun moviesByStudio(studio: String) = bridgeRepository.moviesByStudio(studio)
    fun pagingMovies(movieList: List<Movie>, pageSize: Int = 50) = Pager(
        config = PagingConfig(pageSize),
        pagingSourceFactory = { MoviePagingSource(movieList, pageSize) }
    ).flow.cachedIn(viewModelScope)

    fun movieByID(id: String) = bridgeRepository.movieByID(id)
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
