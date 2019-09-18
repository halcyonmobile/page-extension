package com.halcyonmobile.pagination_extension

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.halcyonmobile.pageui.observeInitialLoadingAndShowIndicator
import com.halcyonmobile.pageui.observeList
import com.halcyonmobile.pageui.observerLoadingAndUpdateSwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val adapter = MainAdapter()
        viewModel.observeList(this, adapter)
        viewModel.observerLoadingAndUpdateSwipeRefreshLayout(this, findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout))
        findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).setOnRefreshListener { viewModel.onForceRefresh() }
        findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
        findViewById<RecyclerView>(R.id.recycler_view).layoutManager = LinearLayoutManager(this)
    }
}
