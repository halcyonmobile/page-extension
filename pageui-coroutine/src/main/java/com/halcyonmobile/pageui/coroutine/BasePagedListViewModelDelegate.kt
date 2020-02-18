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
package com.halcyonmobile.pageui.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.pageui.PagedListViewModel
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Base implementation of [PagedListViewModel].
 *
 * The user should call [setupPagedListByRequest] when they want to start the data accessing.
 */
abstract class BasePagedListViewModelDelegate<Key, Value, Error, PagedResult> : PagedListViewModel<Value, Error> {
    protected val _pagedListResult = MutableLiveData<LiveData<PagedList<Value>>>()
    override val pagedListResult: LiveData<PagedList<Value>> = Transformations.switchMap(_pagedListResult) { it }

    protected val _state = MutableLiveData<DataSourceState<Error>>()
    override val state: LiveData<DataSourceState<Error>> get() = _state

    /**
     * Sets up the delegate to provide the data coming from the given [request]'s pagedResult.
     *
     * Note: setting too big page size with room may result in an undesired behaviour:
     * When the page size is too big, some values might be dropped from the start of the PagedList, which in return can cause scrolling
     * If that scrolling happens it might trigger data loading again and so on.
     */
    abstract suspend fun setupPagedListByRequest(pageSize: Int = DEFAULT_PAGE_SIZE, initialLoadSize: Int = DEFAULT_INITIAL_LOAD_SIZE, request: suspend () -> PagedResult)

    protected fun updatePagedListResult(
        pageSize: Int,
        initialLoadSize: Int,
        dataSourceFactory: DataSource.Factory<Key, Value>,
        boundaryCallback: PagedList.BoundaryCallback<Value>?
    ) {
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
    }

    protected suspend fun listenToStateChannel(stateChannel: ReceiveChannel<DataSourceState<Error>>) {
        for (state in stateChannel) {
            _state.value = state
        }
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 5
        private const val DEFAULT_INITIAL_LOAD_SIZE = 20
    }
}