package com.example.javBridge.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BridgeDao {
    @Insert
    suspend fun addMovie(movie: Movie)

    @Query("SELECT * FROM movie WHERE id=:id")
    fun flowMovie(id: String): Flow<Movie?>

    @Query("SELECT * FROM movie")
    fun flowAllMovies(): Flow<List<Movie>>

    @Query(
        "SELECT * FROM movie WHERE (date Between :start AND :end) " +
                //'%' || :actress || '%'
                "AND (actress IS :actress OR actress LIKE '%' || :actress || '%' ) " +
                "AND (studio IS :studio OR studio=:studio)"
    )
    fun filterMovies(
        start: String,
        end: String,
        actress: String?,
        studio: String?
    ): List<Movie>

    @Query("SELECT * FROM movie WHERE (actress IS NULL OR studio IS NULL) LIMIT (:limit)")
    fun limitMovies(limit: Int = 5): List<Movie>

    @Update
    fun updateMovie(movie: Movie)

    @Insert
    suspend fun addUrl(url: Url)

    @Query("SELECT * FROM url WHERE name=(:name)")
    fun liveUrl(name: String): LiveData<Url?>

    @Query("SELECT * FROM url")
    fun liveUrls(): LiveData<List<Url>>

    @Delete
    suspend fun removeUrl(url: Url)

    @Update
    suspend fun updateUrl(url: Url)
}