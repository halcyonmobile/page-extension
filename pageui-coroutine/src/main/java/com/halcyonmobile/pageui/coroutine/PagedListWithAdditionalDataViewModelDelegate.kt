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
import com.halcyonmobile.page.coroutine.PagedResultWithAdditionalData
import com.halcyonmobile.pageui.PagedListWithAdditionalDataViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Default implementation [PagedListWithAdditionalDataViewModel].
 *
 * The user should call [setupPagedListByRequest] when they want to start the data accessing.
 */
class PagedListWithAdditionalDataViewModelDelegate<Key, Value, AdditionalData, Error> :
    BasePagedListViewModelDelegate<Key, Value, Error, PagedResultWithAdditionalData<Key, Value, AdditionalData, Error>>(),
    PagedListWithAdditionalDataViewModel<Value, AdditionalData, Error> {

    private val _additionalData = MutableLiveData<AdditionalData>()
    override val additionalData: LiveData<AdditionalData> get() = _additionalData

    override suspend fun setupPagedListByRequest(pageSize: Int, initialLoadSize: Int, request: suspend () -> PagedResultWithAdditionalData<Key, Value, AdditionalData, Error>) {
        val (stateChannel, additionalDataChannel, dataSourceFactory, boundaryCallback) = request()
        updatePagedListResult(
            pageSize = pageSize,
            initialLoadSize = initialLoadSize,
            dataSourceFactory = dataSourceFactory,
            boundaryCallback = boundaryCallback
        )


        coroutineScope {
            launch {
                listenToStateChannel(stateChannel)
            }
            launch {
                for (additionalData in additionalDataChannel) {
                    _additionalData.value = additionalData
                }
            }
        }
    }
}