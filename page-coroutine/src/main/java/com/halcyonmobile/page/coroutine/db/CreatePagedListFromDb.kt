/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.coroutine.db.SuspendKeyLocalStorageAdapter
import com.halcyonmobile.page.coroutine.db.SuspendValueLocalCacherAdapter
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendAdditionalDataLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendKeyLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendValueLocalStorage
import com.halcyonmobile.page.db.StateProvidingBoundaryCallBack
import com.halcyonmobile.page.db.ValueToKeyMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

/**
 * Helper function to create a [PagedResult] using database.
 *
 * @param coroutineScope should come from the viewModel
 * @param initialPageKey initial key that should be used if no data is saved in the database
 * @param valueLocalStorage the localStorage that can access the database for values of the page
 * @param keyLocalStorage the localStorage that can access the database for page-keys
 * @param request the call which gets additional data from the network, note: the data is saved via [valueLocalStorage] so you don't need to worry about that.
 * @param networkPageSize how many data should be requested if we are out of local data
 * @param channelBasedDataSourceUpdateListener channel pased update listener which is used to provide the loading state of the [request]
 */
inline fun <Key, Value, reified Error : Throwable> createPagedResultFromDao(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    valueLocalStorage: SuspendValueLocalStorage<Key, Value>,
    keyLocalStorage: SuspendKeyLocalStorage<Key, Value>,
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>,
    networkPageSize: Int = 10,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener()
): PagedResult<Key, Value, Error> {
    val dataSourceFactory = valueLocalStorage.getValueFactory()
    val stateProvidingBoundaryCallback = StateProvidingBoundaryCallBack(
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
        networkPageSize = networkPageSize,
        valueLocalCacher = SuspendValueLocalCacherAdapter(coroutineScope, valueLocalStorage),
        keyLocalStorage = SuspendKeyLocalStorageAdapter(coroutineScope, keyLocalStorage),
        initialKey = initialPageKey,
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size -> request(key, size) }
    )
    return PagedResult(
        dataSourceFactory = dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
        boundaryCallback = stateProvidingBoundaryCallback
    )
}

/**
 * Helper function to create a [PagedResult] using database.
 *
 * @param coroutineScope should come from the viewModel
 * @param initialPageKey initial key that should be used if no data is saved in the database
 * @param valueLocalStorage the localStorage that can access the database for values of the page
 * @param keyLocalMapper A mapper which can extract page-key from the page values
 * @param request the call which gets additional data from the network, note: the data is saved via [valueLocalStorage] so you don't need to worry about that.
 * @param networkPageSize how many data should be requested if we are out of local data
 * @param channelBasedDataSourceUpdateListener channel pased update listener which is used to provide the loading state of the [request]
 */
inline fun <Key, Value, reified Error : Throwable> createPagedResultFromDao(
    coroutineScope: CoroutineScope,
    networkPageSize: Int,
    initialPageKey: Key,
    valueLocalStorage: SuspendValueLocalStorage<Key, Value>,
    keyLocalMapper: ValueToKeyMapper<Key, Value>,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>
): PagedResult<Key, Value, Error> {
    val dataSourceFactory = valueLocalStorage.getValueFactory()
    val stateProvidingBoundaryCallback = StateProvidingBoundaryCallBack(
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
        networkPageSize = networkPageSize,
        valueLocalCacher = SuspendValueLocalCacherAdapter(coroutineScope, valueLocalStorage),
        keyLocalStorage = keyLocalMapper,
        initialKey = initialPageKey,
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size -> request(key, size) }
    )
    return PagedResult(
        dataSourceFactory = dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
        boundaryCallback = stateProvidingBoundaryCallback
    )
}

/**
 * Helper function to create a [PagedResultWithAdditionalData] using database.
 *
 * @param coroutineScope should come from the viewModel
 * @param initialPageKey initial key that should be used if no data is saved in the database
 * @param valueLocalStorage the localStorage that can access the database for values of the page
 * @param keyLocalStorage the localStorage that can access the database for page-keys
 * @param request the call which gets additional data from the network, note: the data is saved via [valueLocalStorage] so you don't need to worry about that.
 * @param networkPageSize how many data should be requested if we are out of local data
 * @param additionalDataLocalStorage the local storage for additional data provided via [PagedResultWithAdditionalData.additionalDataChannel]
 * @param channelBasedDataSourceUpdateListener channel pased update listener which is used to provide the loading state of the [request]
 * @param additionalDataChannel the channel to provide additional data, if you need to update it later on feel free to provide your own channel
 */
inline fun <Key, Value, AdditionalData, reified Error : Throwable> createPagedResultWithAdditionalDataFromDao(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    valueLocalStorage: SuspendValueLocalStorage<Key, Value>,
    keyLocalStorage: SuspendKeyLocalStorage<Key, Value>,
    crossinline request: suspend (Key, Int) -> Triple<List<Value>, Key, AdditionalData?>,
    networkPageSize: Int = 10,
    additionalDataLocalStorage: SuspendAdditionalDataLocalStorage<AdditionalData>,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    additionalDataChannel : ConflatedBroadcastChannel<AdditionalData> = ConflatedBroadcastChannel()
): PagedResultWithAdditionalData<Key, Value, AdditionalData, Error> {
    val dataSourceFactory = valueLocalStorage.getValueFactory()
    coroutineScope.launch {
        additionalDataLocalStorage.get()?.let(additionalDataChannel::offer)
    }
    val stateProvidingBoundaryCallback = StateProvidingBoundaryCallBack(
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
        networkPageSize = networkPageSize,
        valueLocalCacher = SuspendValueLocalCacherAdapter(coroutineScope, valueLocalStorage),
        keyLocalStorage = SuspendKeyLocalStorageAdapter(coroutineScope, keyLocalStorage),
        initialKey = initialPageKey,
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size ->
            val response = request(key, size)
            if (response.third != null){
                @Suppress("UNCHECKED_CAST")
                additionalDataLocalStorage.cache(response.third as AdditionalData)
            }

            response.mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(channel = additionalDataChannel, initialPageKey = initialPageKey, key = key)
        }
    )
    return PagedResultWithAdditionalData(
        dataSourceFactory = dataSourceFactory,
        additionalDataChannel = additionalDataChannel.openSubscription(),
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
        boundaryCallback = stateProvidingBoundaryCallback
    )
}

/**
 * Helper function to create a [PagedResult] using database.
 *
 * @param coroutineScope should come from the viewModel
 * @param initialPageKey initial key that should be used if no data is saved in the database
 * @param valueLocalStorage the localStorage that can access the database for values of the page
 * @param keyLocalMapper A mapper which can extract page-key from the page values
 * @param request the call which gets additional data from the network, note: the data is saved via [valueLocalStorage] so you don't need to worry about that.
 * @param networkPageSize how many data should be requested if we are out of local data
 * @param additionalDataLocalStorage the local storage for additional data provided via [PagedResultWithAdditionalData.additionalDataChannel]
 * @param channelBasedDataSourceUpdateListener channel pased update listener which is used to provide the loading state of the [request]
 * @param additionalDataChannel the channel to provide additional data, if you need to update it later on feel free to provide your own channel
 */
inline fun <Key, Value, AdditionalData, reified Error : Throwable> createPagedResultWithAdditionalDataFromDao(
    coroutineScope: CoroutineScope,
    networkPageSize: Int,
    initialPageKey: Key,
    valueLocalStorage: SuspendValueLocalStorage<Key, Value>,
    keyLocalMapper: ValueToKeyMapper<Key, Value>,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    additionalDataLocalStorage: SuspendAdditionalDataLocalStorage<AdditionalData>,
    crossinline request: suspend (Key, Int) -> Triple<List<Value>, Key, AdditionalData?>,
    additionalDataChannel : ConflatedBroadcastChannel<AdditionalData> = ConflatedBroadcastChannel()
): PagedResult<Key, Value, Error> {
    val dataSourceFactory = valueLocalStorage.getValueFactory()
    coroutineScope.launch {
        additionalDataLocalStorage.get()?.let(additionalDataChannel::offer)
    }
    val stateProvidingBoundaryCallback = StateProvidingBoundaryCallBack(
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
        networkPageSize = networkPageSize,
        valueLocalCacher = SuspendValueLocalCacherAdapter(coroutineScope, valueLocalStorage),
        keyLocalStorage = keyLocalMapper,
        initialKey = initialPageKey,
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size ->
            val response = request(key, size)
            if (response.third != null){
                @Suppress("UNCHECKED_CAST")
                additionalDataLocalStorage.cache(response.third as AdditionalData)
            }

            response.mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(channel = additionalDataChannel, initialPageKey = initialPageKey, key = key)
        }
    )
    return PagedResult(
        dataSourceFactory = dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
        boundaryCallback = stateProvidingBoundaryCallback
    )
}