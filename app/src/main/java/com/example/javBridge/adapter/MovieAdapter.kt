package com.example.javBridge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Movie
import com.example.javBridge.databinding.MovieItemBinding
import com.example.javBridge.holder.MovieHolder
import com.example.javBridge.viewModel.MainViewModel

class MovieAdapter() :
    RecyclerView.Adapter<MovieHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.setOnClickListener {
            if (binding.childRecycler.isVisible) {
                binding.childRecycler.visibility = View.GONE
            } else {
                binding.childRecycler.visibility = View.VISIBLE
            }
        }
        return MovieHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        //必须在bind初始化MovieChildAdapter()，否则parentID不对
        mainViewModel.liveUrls().observe(holder.itemView.context as LifecycleOwner) { urls ->
            holder.childAdapter.setUrls(urls)
        }
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    val movies = mutableListOf<Movie>()
    fun getMovieAtPosition(position: Int): Movie {
        return movies[position]
    }
    fun setMovies(newMovies: List<Movie>) {
        val diffCall = DataDiff(movies, newMovies)
        val diffResult = DiffUtil.calculateDiff(diffCall)
        movies.clear()
        movies.addAll(newMovies)
        diffResult.dispatchUpdatesTo(this)
    }

    //    val urls = mutableListOf<Url>()
    lateinit var mainViewModel: MainViewModel
}

class DataDiff(private val oldList: List<Any>, private val newList: List<Any>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }

}