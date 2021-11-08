package com.example.javBridge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.adapter.UrlAdapter
import com.example.javBridge.database.DatabaseApplication
import com.example.javBridge.databinding.ActivityUrlBinding
import com.example.javBridge.viewModel.UrlViewModel
import com.example.javBridge.viewModel.UrlViewModelFactory

class UrlActivity : AppCompatActivity() {
    private val urlViewModel: UrlViewModel by viewModels {
        UrlViewModelFactory((application as DatabaseApplication).bridgeRepository)
    }
    private val urlAdapter = UrlAdapter()

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
        urlViewModel.liveUrls().observe(this, { urls ->
            if (urlAdapter.itemCount == 0) {
                urlAdapter.urls.addAll(urls)
                urlAdapter.notifyItemRangeInserted(0, urlAdapter.itemCount)
            }
            if (urls.size - urlAdapter.itemCount == 1) {
                urlAdapter.urls.add(urls.last())
                urlAdapter.notifyItemInserted(urlAdapter.itemCount)
            }
        })
        val urlHelper = UrlHelper(urlAdapter, urlViewModel)
        ItemTouchHelper(urlHelper).attachToRecyclerView(urlBinding.urList)
        urlBinding.executePendingBindings()
    }
}


private class UrlHelper(private val adapter: UrlAdapter, private val viewModel: UrlViewModel) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val url = adapter.getUrlAtPosition(viewHolder.adapterPosition)
        viewModel.remove(url)
        adapter.urls.remove(url)
        adapter.notifyItemRemoved(viewHolder.adapterPosition)
    }

}