/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.pageui.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.pageui.PagedListViewModel

/**
 * Default implementation of [PagedListViewModel].
 *
 * The user should call [setupPagedListByRequest] when they want to start the data accessing.
 */
class PagedListViewModelDelegate<Key, Value, Error> : PagedListViewModel<Value, Error> {
    private val _pagedListResult = MutableLiveData<LiveData<PagedList<Value>>>()
    override val pagedListResult: LiveData<PagedList<Value>> = Transformations.switchMap(_pagedListResult) { it }

    private val _state = MutableLiveData<DataSourceState<Error>>()
    override val state: LiveData<DataSourceState<Error>> get() = _state

    suspend fun setupPagedListByRequest(pageSize: Int = DEFAULT_PAGE_SIZE, initialLoadSize: Int = DEFAULT_INITIAL_LOAD_SIZE, request: suspend () -> PagedResult<Key, Value, Error>) {
        val (stateChannel, dataSourceFactory, boundaryCallback) = request()
        _pagedListResult.value = LivePagedListBuilder<Key, Value>(
            dataSourceFactory,
            PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(initialLoadSize)
                .setEnablePlaceholders(false)
                .build()
        )
            .setBoundaryCallback(boundaryCallback)
            .build()

        for (state in stateChannel) {
            _state.value = state
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
        private const val DEFAULT_INITIAL_LOAD_SIZE = 20
    }
}