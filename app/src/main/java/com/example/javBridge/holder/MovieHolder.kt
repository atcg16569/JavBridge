package com.example.javBridge.holder

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.adapter.MovieChildAdapter
import com.example.javBridge.database.Movie
import com.example.javBridge.databinding.MovieItemBinding


class MovieHolder(private val binding: MovieItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val childAdapter = MovieChildAdapter()
    fun bind(movie: Movie, mItemTouchHelper: ItemTouchHelper) {
        binding.id.setOnTouchListener { view, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                mItemTouchHelper.startSwipe(this)
            }
            false
        }
        binding.id.text = "$layoutPosition. ${movie.id}"
        childAdapter.parentID = movie.id
        binding.childRecycler.adapter = childAdapter
        //必须在holder，adapter无效，等同于在xml设置gone。可见性初始化
        binding.childRecycler.visibility = View.GONE
    }
}