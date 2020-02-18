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
package com.halcyonmobile.pagination_extension.shared

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.halcyonmobile.core.Bar
import com.halcyonmobile.pageui.LoadingMorePagedListAdapter
import com.halcyonmobile.pagination_extension.R
import com.halcyonmobile.pagination_extension.databinding.ItemMainViewBinding

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class BarAdapter : LoadingMorePagedListAdapter<Bar, BarAdapter.BarViewHolder>(IntDiffUtilCallback()) {
    override fun getDataItemViewType(position: Int): Int = R.layout.item_main_view

    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder =
        BarViewHolder(parent)

    override fun onBindDataItemViewHolder(holder: BarViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }


    class BarViewHolder(parent: ViewGroup) : BindingViewHolder<ItemMainViewBinding, Bar>(parent,
        R.layout.item_main_view
    ) {

        override fun bindData(data: Bar) {
            binding.data.text = data.title
        }
    }

    class IntDiffUtilCallback : DiffUtil.ItemCallback<Bar>() {
        override fun areItemsTheSame(oldItem: Bar, newItem: Bar): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Bar, newItem: Bar): Boolean =
            oldItem == newItem

    }
}