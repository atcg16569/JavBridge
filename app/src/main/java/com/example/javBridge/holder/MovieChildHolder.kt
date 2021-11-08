package com.example.javBridge.holder

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Url
import com.example.javBridge.databinding.MovieChildItemBinding

class MovieChildHolder(
    private val binding: MovieChildItemBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(url: Url, parentID: String) {
        binding.button.text = url.name
        binding.button.setOnClickListener {
            try {
                val idRegex = Regex("%s")
                //link改变需要重启。若live观察一旦变化,则会自动跳转
                //在父adpter观察即可
                if (url.link.contains("%s")) {
                    val link = idRegex.replace(url.link, parentID)
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    binding.root.context.startActivity(webIntent)
                    Log.d("position", parentID)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "url has no wildcard",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } catch (e: ActivityNotFoundException) {
                e.message?.let { it1 -> Log.e("openWeb", it1) }
            }

        }
    }
}