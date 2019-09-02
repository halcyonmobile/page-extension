package com.halcyonmobile.pagination_extension

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.halcyonmobile.pageui.BindingViewHolder
import com.halcyonmobile.pageui.LoadingMorePagedListAdapter
import com.halcyonmobile.pageui.LoadingMoreViewHolder
import com.halcyonmobile.pagination_extension.databinding.ItemMainViewBinding

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class MainAdapter: LoadingMorePagedListAdapter<Int, MainAdapter.MainViewHolder>(IntDiffUtilCallback()){
    override fun getDataItemViewType(position: Int): Int = R.layout.item_main_view

    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder = MainViewHolder(parent)

    override fun onBindDataItemViewHolder(holder: MainViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }


    class MainViewHolder(parent: ViewGroup) : BindingViewHolder<ItemMainViewBinding>(parent, R.layout.item_main_view){

        fun bind(data: Int){
            binding.data.text = "$data"
        }
    }

    class IntDiffUtilCallback : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem

    }
}