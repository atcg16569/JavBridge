package com.example.javBridge.holder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Url
import com.example.javBridge.databinding.UrlItemBinding
import com.example.javBridge.viewModel.UrlViewModel

class UrlHolder(private val binding: UrlItemBinding, private val viewModel: UrlViewModel) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(url: Url) {
        binding.name.text = url.name
        binding.link.hint = url.link
        binding.link.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.link.setText(url.link)
                binding.link.setSelection(binding.link.length())
                binding.change.visibility = View.VISIBLE
                binding.cancel.visibility = View.VISIBLE
            } else {
                binding.change.visibility = View.GONE
                binding.cancel.visibility = View.GONE
                binding.link.text.clear()
            }
        }
        binding.cancel.setOnClickListener {
            binding.link.clearFocus()
            binding.link.hint = url.link
            //需要手动收起键盘
        }
        binding.change.setOnClickListener {
            val newLink = binding.link.text.toString()
            if (newLink != "" && url.link != newLink) {
                url.link = newLink
                viewModel.update(url)
                Log.d("UrlUpdate", "${url.name} update $newLink")
            }
            //must clear at last,if before,always null text
            binding.link.clearFocus()
            binding.link.hint = newLink
        }
        //可见性初始化
        binding.change.visibility = View.GONE
        binding.cancel.visibility = View.GONE
    }
}