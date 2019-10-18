package com.halcyonmobile.pagination_extension

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.halcyonmobile.core.Bar
import com.halcyonmobile.pageui.LoadingMorePagedListAdapter
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

    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder = BarViewHolder(parent)

    override fun onBindDataItemViewHolder(holder: BarViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }


    class BarViewHolder(parent: ViewGroup) : BindingViewHolder<ItemMainViewBinding, Bar>(parent, R.layout.item_main_view) {

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