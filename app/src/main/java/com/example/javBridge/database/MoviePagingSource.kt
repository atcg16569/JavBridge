package com.example.javBridge.database

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException

class MoviePagingSource(private val movieList: List<Movie>) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        // Load as many items as hinted by params.loadSize
        return try{
            LoadResult.Page(
                data = movieList,
                prevKey = null,
                nextKey = null
            )
        }catch (exception: IOException) {
            return LoadResult.Error(exception)
        } /*catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }*/
    }
}