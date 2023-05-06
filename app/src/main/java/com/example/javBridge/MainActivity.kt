package com.example.javBridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.example.javBridge.adapter.PagingMovieAdapter
import com.example.javBridge.database.DatabaseApplication
import com.example.javBridge.databinding.ActivityMainBinding
import com.example.javBridge.getFrom.MovieText
import com.example.javBridge.viewModel.MainViewModel
import com.example.javBridge.viewModel.MainViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as DatabaseApplication).bridgeRepository)
    }
    private val pagingMovieAdapter = PagingMovieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.mainViewModel = mainViewModel
        mainBinding.list.adapter = pagingMovieAdapter
        setSupportActionBar(mainBinding.toolbar)
        val drawerToggle =
            ActionBarDrawerToggle(this, mainBinding.drawer, mainBinding.toolbar, 0, 0)
        drawerToggle.syncState()
        mainBinding.drawer.addDrawerListener(drawerToggle)
        pagingMovieAdapter.mainViewModel = mainViewModel
        mainViewModel.flowAllMovies().asLiveData().observe(this) { allMovies ->
            mainViewModel.viewModelScope.launch {
                mainViewModel.pagingMovies(allMovies).collectLatest {
                    pagingMovieAdapter.submitData(it)
                }
            }
        }
        mainViewModel.liveFilter.observe(this) { filterMovies ->
            if (filterMovies.isNotEmpty()) {
                mainViewModel.viewModelScope.launch {
                    mainViewModel.pagingMovies(filterMovies).collectLatest {
                        pagingMovieAdapter.submitData(it)
                    }
                }
            } else {
                mainViewModel.viewModelScope.launch {
                    mainViewModel.flowAllMovies().collect { allMovies ->
                        mainViewModel.pagingMovies(allMovies).collectLatest {
                            pagingMovieAdapter.submitData(it)
                        }
                    }
                }
                Toast.makeText(this, "filter empty!", Toast.LENGTH_LONG).show()
            }
        }
        val movieHelperCallback = MovieHelperCallback(pagingMovieAdapter, mainViewModel)
        val movieHelper = ItemTouchHelper(movieHelperCallback)
        pagingMovieAdapter.itemTouchHelper = movieHelper
        movieHelper.attachToRecyclerView(mainBinding.list)
        lifecycleScope.launch {
            pagingMovieAdapter.loadStateFlow.collectLatest { loadStates ->
                Log.d("loadState", loadStates.source.toString())
            }
        }
        mainBinding.executePendingBindings()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("movie_refresh")
            .observe(this) { info ->
                val button = menu.findItem(R.id.worker)
                if (info.isNotEmpty()) {
                    button.title = info[0].state.toString()
                } else {
                    button.title = "schedule"
                }
            }
        return true
//        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.importJson -> {
                getContent.launch("application/json")
                true
            }

            R.id.configure -> {
                val intent = Intent(this, UrlActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.explore -> {
                val intent = Intent(this, WebActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.export -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "backup_bridge.json")
                }
                writeMovies.launch(intent)
                true
            }

            R.id.worker -> {
                val manager = WorkManager.getInstance(this)
                val state = item.title
                if (state == "schedule" || state == "CANCELLED") {
                    manager.enqueueUniquePeriodicWork(
                        "movie_refresh",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        javRequest
                    )
                    Toast.makeText(this, "Worker Enqueued", Toast.LENGTH_LONG)
                        .show()
                } else {
                    manager.cancelUniqueWork("movie_refresh")
                    Toast.makeText(this, "Worker Canceled", Toast.LENGTH_LONG)
                        .show()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
//        return super.onOptionsItemSelected(item)
    }

    //选择json文件
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                for (u in uris) {
                    val name = File(u.path!!).name
                    Log.d("import file", name)
                    val host = name.substringBefore(".json")
                    val text = readTextFromUri(u)
                    val list = if (host.startsWith("backup_bridge")) {
                        MovieText().restore(text)
                    } else {
                        when (host.endsWith("_javbus")) {
                            true -> MovieText().bus(text)
                            false -> MovieText().db(text)
                        }
                    }
                    mainViewModel.viewModelScope.launch(Dispatchers.IO) {
                        for (m in list) {
                            if (mainViewModel.movieByID(m.id) == null) {
                                mainViewModel.susAdd(m)
                            }
                        }
                    }
                }
            }
        }

    // 备份json文件
    private val writeMovies =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    mainViewModel.viewModelScope.launch {
                        mainViewModel.flowAllMovies().collect {
                            writeDocument(uri, Json.encodeToString(it))
                        }
                    }
                }
            }
        }

    // 读取字符串
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    // 修改文档
    private fun writeDocument(uri: Uri, content: String) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { output ->
                    output.write(content.toByteArray())
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class MovieHelperCallback(
    private val adapter: PagingMovieAdapter,
    private val viewModel: MainViewModel
) :
    ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val movie = adapter.getMovieAtPosition(viewHolder.layoutPosition)
        if (movie != null) {
            viewModel.removeMovie(movie)
            adapter.notifyItemRemoved(viewHolder.layoutPosition)
            Toast.makeText(viewHolder.itemView.context, "Delete ${movie.id}", Toast.LENGTH_SHORT)
                .show()
            Log.d(
                "swipe position",
                "absolutePosition:${viewHolder.absoluteAdapterPosition}\nbindingPosition:${viewHolder.bindingAdapterPosition}\nlayoutPosition:${viewHolder.layoutPosition}"
            )
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
}