/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.pageui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Base [PagedListAdapter] which handles loading more and loading more actions as list item.
 *
 * When [addLoadingMoreIndicator] is called the last item of the RecyclerView will be a loading more view
 * When [addLoadingMoreFailedIndicator] is called the last item of the RecyclerView will be a loading more error view with possibility of retry
 * When [removeLoadingMoreIndicator] is called this extra views are removed.
 *
 * By default it uses the [LoadingMoreViewHolder] & [LoadingMoreFailedViewHolder] to show the states, however this is customizable via [onCreateLoadingMoreViewHolder],
 * [onCreateLoadingMoreFailedViewHolder], [onBindLoadingMoreViewHolder] and [onBindLoadingMoreFailedViewHolder].
 *
 * Note: If you have multiple viewTypes and you are not using resourceIds as ViewType, you should overwrite [loadingMoreViewType] & [loadingMoreFailedViewType] to make sure there
 * is no collusion.
 */
abstract class LoadingMorePagedListAdapter<Data, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<Data>) :
    PagedListAdapter<Data, RecyclerView.ViewHolder>(diffCallback) {

    private var state: State? = null
    open val loadingMoreViewType: Int = LoadingMoreViewHolder.layoutRes
    open val loadingMoreFailedViewType: Int = LoadingMoreFailedViewHolder.layoutRes

    fun addLoadingMoreIndicator() = changeToState(State.LoadingMore)

    fun removeLoadingMoreIndicator() = changeToState(null)

    fun addLoadingMoreFailedIndicator(retry: () -> Unit) = changeToState(State.LoadingMoreFailed(retry))

    private fun changeToState(stateToChangeTo: State?) {
        if (state == stateToChangeTo) return

        val currentState = state
        this.state = stateToChangeTo
        when {
            currentState == null -> notifyItemInserted(super.getItemCount())
            stateToChangeTo == null -> notifyItemRemoved(super.getItemCount())
            else -> notifyItemChanged(super.getItemCount())
        }
    }

    final override fun getItemViewType(position: Int): Int {
        val state = state
        return if (position == super.getItemCount() && state != null) {
            when (state) {
                State.LoadingMore -> loadingMoreViewType
                is State.LoadingMoreFailed -> loadingMoreFailedViewType
            }
        } else {
            getDataItemViewType(position)
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            loadingMoreViewType -> onCreateLoadingMoreViewHolder(parent)
            loadingMoreFailedViewType -> onCreateLoadingMoreFailedViewHolder(parent)
            else -> onCreateDataItemViewHolder(parent, viewType)
        }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val state = state
        when {
            position == super.getItemCount() && state == State.LoadingMore -> onBindLoadingMoreViewHolder(holder, position)
            position == super.getItemCount() && state is State.LoadingMoreFailed -> onBindLoadingMoreFailedViewHolder(holder, position, state)
            else ->
                @Suppress("UNCHECKED_CAST")
                onBindDataItemViewHolder(holder as VH, position)
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (position == super.getItemCount() && state != null) {
            onBindViewHolder(holder, position)
        } else {
            @Suppress("UNCHECKED_CAST")
            onBindDataItemViewHolder(holder as VH, position, payloads)
        }
    }

    final override fun getItemCount(): Int = super.getItemCount() + if (state != null) 1 else 0

    fun notifyItemByDataIndex(index: Int) {
        notifyItemChanged(index + itemCount - super.getItemCount())
    }

    open fun onCreateLoadingMoreViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = LoadingMoreViewHolder(parent)

    open fun onBindLoadingMoreViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    open fun onCreateLoadingMoreFailedViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = LoadingMoreFailedViewHolder(parent)

    open fun onBindLoadingMoreFailedViewHolder(holder: RecyclerView.ViewHolder, position: Int, state: State.LoadingMoreFailed) =
        (holder as LoadingMoreFailedViewHolder).bind(state.retry)

    abstract fun getDataItemViewType(position: Int): Int

    abstract fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract fun onBindDataItemViewHolder(holder: VH, position: Int)

    open fun onBindDataItemViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) = onBindDataItemViewHolder(holder, position)

    sealed class State {
        object LoadingMore : State()
        class LoadingMoreFailed(val retry: () -> Unit) : State()
    }
}