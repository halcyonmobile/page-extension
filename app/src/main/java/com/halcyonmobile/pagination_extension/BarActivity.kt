package com.halcyonmobile.pagination_extension

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.halcyonmobile.pageui.observeInitialLoadingAndShowIndicator
import com.halcyonmobile.pageui.observeList
import com.halcyonmobile.pageui.observerLoadingAndUpdateSwipeRefreshLayout
import com.halcyonmobile.pagination_extension.databinding.ActivityMainBinding

class BarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(BarViewModel::class.java)
        viewModel.context = this
        viewModel.a()
        val adapter = BarAdapter()
        viewModel.observeList(this, adapter)
        viewModel.observerLoadingAndUpdateSwipeRefreshLayout(this, binding.swipeRefreshLayout)
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.onForceRefresh() }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
