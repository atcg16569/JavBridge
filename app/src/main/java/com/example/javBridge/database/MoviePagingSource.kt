package com.example.javBridge.database

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.math.max

private const val STARTING_KEY = 0

class MoviePagingSource(private val movieList: List<Movie>, private val pageSize:Int) : PagingSource<Int, Movie>() {
    private var countLoadMovies = 0
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    // TODO 后页删除movie丢失位置
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val chunkList = if (params.loadSize < movieList.size) {
            movieList.chunked(params.loadSize)
        } else {
            listOf(movieList)
        }
        val index = params.key ?: STARTING_KEY
        val end = chunkList.lastIndex
        countLoadMovies += chunkList[index].size
        Log.d(
            "load cut",
            "totalMovies:${movieList.size},loadSize:${params.loadSize},key:${params.key},loadedMovies:$countLoadMovies"
        )
        return try {
            LoadResult.Page(
                data = chunkList[index],
                prevKey = when (index) {
                    STARTING_KEY -> null
                    else -> ensureValidKey(key = index - params.loadSize)
                },
                nextKey = when (index) {
                    end -> null
                    // 初始加载大小为 3 * PAGE_SIZE
                    // 要保证我们在第二次加载时不会去请求重复的项目。
                    else -> index + params.loadSize / pageSize
                }
            )
        } catch (exception: Exception) {
            Log.e("PagingSource", exception.message.toString())
            return LoadResult.Error(exception)
        } /*catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }*/
    }
}