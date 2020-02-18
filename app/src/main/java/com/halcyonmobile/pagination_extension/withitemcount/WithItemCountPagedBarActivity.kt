/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
