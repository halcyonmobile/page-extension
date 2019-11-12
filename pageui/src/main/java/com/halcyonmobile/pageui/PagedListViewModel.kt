/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pageui

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.halcyonmobile.page.DataSourceState

/**
 * Simple interface for ViewModels which provides paginated data.
 */
interface PagedListViewModel<Value, Error> {
    val pagedListResult: LiveData<PagedList<Value>>
    val state: LiveData<DataSourceState<Error>>
}

fun <Value> PagedListViewModel<Value, *>.observeList(
    lifecycleOwner: LifecycleOwner,
    loadingMorePagedListAdapter: LoadingMorePagedListAdapter<Value, *>
) {
    pagedListResult.observe(lifecycleOwner, Observer<PagedList<Value>> {
        loadingMorePagedListAdapter.submitList(it)
    })
    state.observe(lifecycleOwner, Observer {
        when (it) {
            is DataSourceState.LoadingMore -> loadingMorePagedListAdapter.addLoadingMoreIndicator()
            is DataSourceState.ErrorLoadingMore -> loadingMorePagedListAdapter.addLoadingMoreFailedIndicator(it::retry)
            is DataSourceState.InitialLoading,
            is DataSourceState.EndReached -> loadingMorePagedListAdapter.removeLoadingMoreIndicator()
            is DataSourceState.Normal,
            is DataSourceState.Empty,
            is DataSourceState.ErrorLoadingInitial,
            null -> Unit
        }
    })
}

fun <Value> PagedListViewModel<Value, *>.observeInitialLoadingAndShowIndicator(
    lifecycleOwner: LifecycleOwner,
    loadingState: View
) {
    state.observe(lifecycleOwner, Observer {
        loadingState.visibility =
            if (it is DataSourceState.InitialLoading<*>) View.VISIBLE else View.GONE
    })
}

fun <Value> PagedListViewModel<Value, *>.observerLoadingAndUpdateSwipeRefreshLayout(
    lifecycleOwner: LifecycleOwner,
    swipeRefreshLayout: SwipeRefreshLayout
) {
    state.observe(lifecycleOwner, Observer {
        swipeRefreshLayout.isRefreshing = it is DataSourceState.InitialLoading
    })
}

fun <Value> PagedListViewModel<Value, *>.observeEmptyStateAndShowIndicator(
    lifecycleOwner: LifecycleOwner,
    emptyState: View
) {
    state.observe(lifecycleOwner, Observer {
        emptyState.visibility = if (it is DataSourceState.Empty) View.VISIBLE else View.GONE
    })
}

inline fun <Value> PagedListViewModel<Value, *>.observeInitialErrorAndShowIndicator(
    lifecycleOwner: LifecycleOwner,
    errorState: View,
    crossinline setupRetry: (() -> Unit) -> Unit
) {
    state.observe(lifecycleOwner, Observer {
        errorState.visibility =
            if (it is DataSourceState.ErrorLoadingInitial<*>) View.VISIBLE else View.GONE
        if (it is DataSourceState.ErrorLoadingInitial<*>) {
            setupRetry { it.retry() }
        }
    })
}