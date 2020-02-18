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
