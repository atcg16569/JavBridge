package com.example.javBridge

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.adapter.UrlAdapter
import com.example.javBridge.database.DatabaseApplication
import com.example.javBridge.databinding.ActivityUrlBinding
import com.example.javBridge.getFrom.readTextFromUri
import com.example.javBridge.getFrom.url
import com.example.javBridge.getFrom.writeDocument
import com.example.javBridge.viewModel.UrlViewModel
import com.example.javBridge.viewModel.UrlViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class UrlActivity : AppCompatActivity() {
    private val urlViewModel: UrlViewModel by viewModels {
        UrlViewModelFactory((application as DatabaseApplication).bridgeRepository)
    }
    private val urlAdapter = UrlAdapter()
    private val writeUrls =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    urlViewModel.viewModelScope.launch {
                        urlViewModel.flowUrls().collect {
                            writeDocument(uri, Json.encodeToString(it), contentResolver)
                        }
                    }
                }
            }
        }
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                for (u in uris) {
                    val name = File(u.path!!).name
                    Log.d("import file", name)
                    val host = name.substringBefore(".json")
                    val text = readTextFromUri(u, contentResolver)
                    if (host.contains("urlConfig")) {
                        val uList = url(text)
                        urlViewModel.viewModelScope.launch(Dispatchers.IO) {
                            for (ur in uList) {
                                if (urlViewModel.urlByName(ur.name) == null) {
                                    urlViewModel.add(ur)
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val urlBinding: ActivityUrlBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_url)
        urlBinding.addUrl.setOnClickListener {
            UrlDialogFragment(urlViewModel).show(supportFragmentManager, "addUrl")
        }
        urlBinding.urlViewModel = urlViewModel
        urlBinding.urList.adapter = urlAdapter
        urlAdapter.urlViewModel = urlViewModel
        urlViewModel.flowUrls().asLiveData().observe(this) { urls ->
            if (urlAdapter.itemCount == 0) {
                urlAdapter.urls.addAll(urls)
                urlAdapter.notifyItemRangeInserted(0, urlAdapter.itemCount)
            }
            if (urls.size - urlAdapter.itemCount == 1) {
                urlAdapter.urls.add(urls.last())
                urlAdapter.notifyItemInserted(urlAdapter.itemCount)
            }
        }
        val urlHelper = UrlHelperCallback(urlAdapter, urlViewModel)
        ItemTouchHelper(urlHelper).attachToRecyclerView(urlBinding.urList)
        urlBinding.executePendingBindings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.url_menu, menu)
        return true
//        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.importUrl -> {
                getContent.launch("application/json")
                true
            }

            R.id.export -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "bridge_urlConfig.json")
                }
                writeUrls.launch(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}


private class UrlHelperCallback(
    private val adapter: UrlAdapter,
    private val viewModel: UrlViewModel
) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val url = adapter.getUrlAtPosition(viewHolder.layoutPosition)
        viewModel.remove(url)
        adapter.urls.remove(url)
        adapter.notifyItemRemoved(viewHolder.layoutPosition)
    }

}