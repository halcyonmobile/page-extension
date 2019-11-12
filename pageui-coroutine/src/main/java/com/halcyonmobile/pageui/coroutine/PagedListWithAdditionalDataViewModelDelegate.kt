/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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