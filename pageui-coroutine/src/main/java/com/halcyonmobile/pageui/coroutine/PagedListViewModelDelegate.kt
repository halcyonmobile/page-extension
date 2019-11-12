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
package com.halcyonmobile.pageui.coroutine

import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.pageui.PagedListViewModel

/**
 * Default implementation of [PagedListViewModel].
 *
 * The user should call [setupPagedListByRequest] when they want to start the data accessing.
 */
class PagedListViewModelDelegate<Key, Value, Error> : BasePagedListViewModelDelegate<Key, Value, Error, PagedResult<Key, Value, Error>>() {

    override suspend fun setupPagedListByRequest(pageSize: Int, initialLoadSize: Int, request: suspend () -> PagedResult<Key, Value, Error>) {
        val (stateChannel, dataSourceFactory, boundaryCallback) = request()
        updatePagedListResult(
            pageSize = pageSize,
            initialLoadSize = initialLoadSize,
            dataSourceFactory = dataSourceFactory,
            boundaryCallback = boundaryCallback)

        listenToStateChannel(stateChannel)
    }
}