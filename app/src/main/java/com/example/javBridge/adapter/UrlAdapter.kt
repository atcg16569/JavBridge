package com.example.javBridge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Url
import com.example.javBridge.databinding.UrlItemBinding
import com.example.javBridge.holder.UrlHolder
import com.example.javBridge.viewModel.UrlViewModel

class UrlAdapter : RecyclerView.Adapter<UrlHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlHolder {
        val binding = UrlItemBinding.inflate(LayoutInflater.from(parent.context))
        return UrlHolder(binding, urlViewModel)
    }

    override fun onBindViewHolder(holder: UrlHolder, position: Int) {
        holder.bind(urls[position])
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    val urls = mutableListOf<Url>()
    fun getUrlAtPosition(position: Int): Url {
        return urls[position]
    }

    lateinit var urlViewModel: UrlViewModel
}