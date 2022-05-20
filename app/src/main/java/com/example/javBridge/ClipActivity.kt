package com.example.javBridge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.example.javBridge.database.DatabaseApplication
import com.example.javBridge.database.Movie
import com.example.javBridge.viewModel.ClipViewModel
import com.example.javBridge.viewModel.ClipViewModelFactory


class ClipActivity : AppCompatActivity() {
    private val clipViewModel: ClipViewModel by viewModels {
        ClipViewModelFactory((application as DatabaseApplication).bridgeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip)
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
            val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            val id = selectedText.trim()
            val clipText: TextView = findViewById(R.id.clipText)
            clipViewModel.flowMovie(id).asLiveData().observe(this) {
                if (it == null) {
                    Toast.makeText(this,"add $id",Toast.LENGTH_LONG).show()
                    clipViewModel.addMovie(Movie(id))
                } else {
                    clipText.text = "$id existed"
                }
            }
        }
    }
}