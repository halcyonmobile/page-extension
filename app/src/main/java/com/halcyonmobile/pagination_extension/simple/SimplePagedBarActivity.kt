/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.simple

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.halcyonmobile.pageui.observeList
import com.halcyonmobile.pageui.observerLoadingAndUpdateSwipeRefreshLayout
import com.halcyonmobile.pagination_extension.R
import com.halcyonmobile.pagination_extension.databinding.ActivitySimplePagedBarBinding
import com.halcyonmobile.pagination_extension.shared.BarAdapter

class SimplePagedBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySimplePagedBarBinding>(this, R.layout.activity_simple_paged_bar)
        val simpleRepositoryFactory = SimpleRepositoryFactory(this)
        val viewModel = SimplePagedBarViewModel(simpleRepositoryFactory.get(intent.screenType))
        viewModel.loadData()
        val adapter = BarAdapter()
        viewModel.observeList(this, adapter)
        viewModel.observerLoadingAndUpdateSwipeRefreshLayout(this, binding.swipeRefreshLayout)
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.onForceRefresh() }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    companion object {

        private var Intent.screenType: SimpleRepositoryFactory.Type
            set(value) {
                putExtra("TYPE", value)
            }
            get() = getSerializableExtra("TYPE") as SimpleRepositoryFactory.Type

        fun getStartActivityIntent(context: Context, type: SimpleRepositoryFactory.Type): Intent =
            Intent(context, SimplePagedBarActivity::class.java).apply {
                screenType = type
            }
    }
}
