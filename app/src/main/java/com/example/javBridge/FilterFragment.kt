package com.example.javBridge

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.javBridge.database.Movie
import com.example.javBridge.databinding.FragmentFilterBinding
import com.example.javBridge.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val filterBinding: FragmentFilterBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)
        filterBinding.filter.setOnClickListener {
            val from = filterBinding.from.text
            val to = filterBinding.to.text
            val actor = filterBinding.actor.text
            val studioT = filterBinding.studio.text
            val actress = mutableSetOf<String?>()
            if (actor.isNotBlank()) {
                actress.addAll(actor.toString().split(","))
                //末尾为逗号时，split为空字符，like query匹配所有字符，需移除
                actress.removeIf {
                    it.isNullOrBlank()
                }
            } else {
                actress.add(null)
            }
            val studio = mutableSetOf<String?>()
            if (studioT.isNotBlank()) {
                studio.addAll(studioT.toString().split(","))
                studio.removeIf {
                    it.isNullOrBlank()
                }
            } else {
                studio.add(null)
            }

            val flowMovies = flow {
                for (a in actress) {
                    for (s in studio) {
                        emit(viewModel.filter(from.toString(), to.toString(), a, s))
                    }
                }
            }
            val movies = mutableListOf<Movie>()
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                flowMovies.collect { result ->
                    movies.addAll(result)
                }
                Log.d("results", movies.toString())
                withContext(Dispatchers.Main){//viewModel.viewModelScope.launch {
                    viewModel.liveFilter.value = movies
                }
            }
        }
        return filterBinding.root
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_filter, container, false)
    }
}