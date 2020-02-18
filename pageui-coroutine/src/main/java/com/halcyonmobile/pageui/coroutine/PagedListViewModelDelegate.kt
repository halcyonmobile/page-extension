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