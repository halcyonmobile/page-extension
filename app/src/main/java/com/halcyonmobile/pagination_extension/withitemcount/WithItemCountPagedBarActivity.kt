/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.withitemcount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halcyonmobile.pageui.observeList
import com.halcyonmobile.pageui.observerLoadingAndUpdateSwipeRefreshLayout
import com.halcyonmobile.pagination_extension.R
import com.halcyonmobile.pagination_extension.databinding.ActivityWithItemCountPagedBarBinding
import com.halcyonmobile.pagination_extension.db.WithItemCountPagedBarViewModel
import com.halcyonmobile.pagination_extension.shared.BarAdapter

class WithItemCountPagedBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityWithItemCountPagedBarBinding>(this, R.layout.activity_with_item_count_paged_bar)
        val simpleRepositoryFactory = WithItemCountRepositoryFactory(this)
        val viewModel = WithItemCountPagedBarViewModel(simpleRepositoryFactory.get(intent.screenType))
        viewModel.loadData()
        val adapter = BarAdapter()
        viewModel.observeList(this, adapter)
        viewModel.observerLoadingAndUpdateSwipeRefreshLayout(this, binding.swipeRefreshLayout)
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.onForceRefresh() }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.itemCount.observe(this, Observer {
            binding.itemCount.text = it?.let { "itemCount: $it" }
        })
    }

    companion object {

        private var Intent.screenType: WithItemCountRepositoryFactory.Type
            set(value) {
                putExtra("TYPE", value)
            }
            get() = getSerializableExtra("TYPE") as WithItemCountRepositoryFactory.Type

        fun getStartActivityIntent(context: Context, type: WithItemCountRepositoryFactory.Type): Intent =
            Intent(context, WithItemCountPagedBarActivity::class.java).apply {
                screenType = type
            }
    }
}
