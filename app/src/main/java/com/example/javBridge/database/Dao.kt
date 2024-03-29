package com.example.javBridge.database

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BridgeDao {

    @Insert
    suspend fun addMovie(movie: Movie)

    @Delete
    suspend fun removeMovie(movie: Movie)

    @Query("SELECT * FROM movie WHERE id=:id")
    fun flowMovie(id: String): Flow<Movie?>

    @Query("SELECT * FROM movie WHERE id=:id")
    fun movieByID(id: String): Movie?

    @Query("SELECT * FROM movie")
    fun flowAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movie WHERE (date Between :start AND :end) ")
    fun moviesByDate(start: String, end: String): List<Movie>

    @Query("SELECT * FROM movie WHERE (actress IS :actress OR actress LIKE '%' || :actress || '%' ) ")
    fun moviesByActress(actress: String): List<Movie>

    @Query("SELECT * FROM movie WHERE (studio IS :studio OR studio like '%'||:studio||'%')")
    fun moviesByStudio(studio: String): List<Movie>

    @Query("SELECT * FROM movie WHERE (actress IS NULL AND studio IS NULL) LIMIT (:limit)")
    fun limitMovies(limit: Int = 5): List<Movie>

    @Query("SELECT * FROM movie WHERE (studio like 'status_%' ) LIMIT (:limit)")
    fun failedMovies(limit: Int = 5): List<Movie>

    @Update
    fun updateMovie(movie: Movie)

    @Insert
    suspend fun addUrl(url: Url)

    @Query("SELECT * FROM url WHERE name=(:name)")
    fun urlByName(name: String): Url?

    @Query("SELECT * FROM url")
    fun flowUrls(): Flow<List<Url>>

    @Delete
    suspend fun removeUrl(url: Url)

    @Update
    suspend fun updateUrl(url: Url)

    @Query("SELECT * FROM movie")
    fun pagingMovies(): PagingSource<Int, Movie>
}
