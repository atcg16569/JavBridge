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
    ): View {
        val filterBinding: FragmentFilterBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)
        filterBinding.filter.setOnClickListener {
            val inputMap =
                mutableMapOf("date" to true, "actress" to true, "studio" to true)
            val from = filterBinding.from.text
            val to = filterBinding.to.text
            val actor = filterBinding.actor.text
            val studioT = filterBinding.studio.text
            val actress = mutableSetOf<String>()
            if (actor.isNotBlank()) {
                actress.addAll(actor.toString().split(","))
                //末尾为逗号时，split为空字符，like query匹配所有字符，需移除
                actress.removeIf {
                    it.isBlank()
                }
            } else {
                inputMap["actress"] = false
            }
            val studio = mutableSetOf<String>()
            if (studioT.isNotBlank()) {
                studio.addAll(studioT.toString().split(","))
                studio.removeIf {
                    it.isBlank()
                }
            } else {
                inputMap["studio"] = false
            }
            if (from.isBlank() && to.isBlank()) {
                inputMap["date"] = false
            }

            val movies = mutableListOf<Movie>()
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val movieMap = mutableMapOf<String, List<Movie>>()
                inputMap.forEach {
                    if (it.value) {
                        when (it.key) {
                            "date" -> movieMap[it.key] =
                                viewModel.moviesByDate(from.toString(), to.toString())
                            "actress" -> {
                                val actList = mutableListOf<Movie>()
                                for (a in actress) {
                                    actList.addAll(viewModel.moviesByActress(a))
                                }
                                movieMap[it.key] = actList
                            }
                            "studio" -> {
                                val stuList = mutableListOf<Movie>()
                                for (s in studio) {
                                    stuList.addAll(viewModel.moviesByStudio(s))
                                }
                                movieMap[it.key] = stuList
                            }
                        }
                    }
                }
                // 优化foreach
                if (movieMap.isNotEmpty()){
                    val lists = movieMap.values
                    when (movieMap.size){
                        1 -> movies.addAll(lists.elementAt(0))
                        2 -> movies.addAll(lists.elementAt(0) intersect lists.elementAt(1))
                        3 -> movies.addAll(lists.elementAt(0) intersect lists.elementAt(1) intersect lists.elementAt(2))
                    }
                }
                Log.d("results", movies.toString())
                withContext(Dispatchers.Main) {//viewModel.viewModelScope.launch {
                    viewModel.liveFilter.value = movies
                }
            }
        }
        return filterBinding.root
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_filter, container, false)
    }
}