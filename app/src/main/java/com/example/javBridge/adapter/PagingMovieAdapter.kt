package com.example.javBridge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.javBridge.database.Movie
import com.example.javBridge.databinding.MovieItemBinding
import com.example.javBridge.holder.MovieHolder
import com.example.javBridge.viewModel.MainViewModel

class PagingMovieAdapter : PagingDataAdapter<Movie, MovieHolder>(diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
    }

    lateinit var mainViewModel: MainViewModel
    fun getMovieAtPosition(position: Int): Movie? {
        return getItem(position)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        mainViewModel.liveUrls().observe(holder.itemView.context as LifecycleOwner) { urls ->
            holder.childAdapter.setUrls(urls)
        }
        getItem(position)?.let { holder.bind(it) }
    }

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
}