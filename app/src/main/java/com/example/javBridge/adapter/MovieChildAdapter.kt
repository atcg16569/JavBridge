package com.example.javBridge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Url
import com.example.javBridge.databinding.MovieChildItemBinding
import com.example.javBridge.holder.MovieChildHolder

class MovieChildAdapter : RecyclerView.Adapter<MovieChildHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieChildHolder {
        val binding = MovieChildItemBinding.inflate(LayoutInflater.from(parent.context))
//        binding.lifecycleOwner = parent.context as LifecycleOwner
        return MovieChildHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieChildHolder, position: Int) {
        holder.bind(urls[position], parentID)
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    private val urls = mutableListOf<Url>()
    lateinit var parentID: String
    companion object {
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
    }
    fun setUrls(newUrls: List<Url>) {
        val diffCall = DataDiff(urls, newUrls)
        val diffResult = DiffUtil.calculateDiff(diffCall)
        urls.clear()
        urls.addAll(newUrls)
        diffResult.dispatchUpdatesTo(this)
    }
}