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

package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.DataSourceAggregatingDataSourceFactory
import com.halcyonmobile.page.DataSourceInvalidator
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideCacheByKey
import com.halcyonmobile.page.ProvideCacheByKeyAndPageSizeState
import com.halcyonmobile.page.ProvideCacheWithAdditionalDataByKey
import com.halcyonmobile.page.StateProvidingListDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel

/**
 * Helper function which sets up your [PagedResult] with coroutine based [DataSourceUpdateListener] & [SuspendProvideDataByPagedKeyAndSize]
 *
 * @param coroutineScope in which [request] will be run and [ChannelBasedDataSourceUpdateListener] updated
 * @param initialPageKey is the key which will be first provided to the [request]
 * @param request the requests to get the next page, it returns the list of values and the next key.
 * @param channelBasedDataSourceUpdateListener request state update listener, it's automatically updated while the requests are running / failing / succeeding etc.
 * @param dataSourceInvalidator a way to invalidate the latest datasource.
 * @param cache a cache which used by the datasource to gather local values
 *
 * The dataSource works the following way:
 *  - is there a cache? there is, then return that
 *  - no cache? call the [request]
 *  - depending on the response of the [request] update the [channelBasedDataSourceUpdateListener] and send the next page
 */
inline fun <Key, Value, reified Error : Throwable> createPagedResultFromRequest(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>? = null,
    crossinline cache: (Key, Int) -> Pair<List<Value>, Key>? = { _, _ -> null }
): PagedResult<Key, Value, Error> {
    val dataSourceFactory = StateProvidingListDataSourceFactory<Key, Value, Error>(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKey { key, size -> cache(key, size) },
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size -> request(key, size) },
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResult(
        boundaryCallback = null,
        dataSourceFactory = dataSourceInvalidator?.let { DataSourceAggregatingDataSourceFactory(dataSourceFactory, it) } ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}

/**
 * Helper function which sets up your [PagedResult] with coroutine based [DataSourceUpdateListener] & [SuspendProvideDataByPagedKeyAndSize]
 *
 * @param coroutineScope in which [request] will be run and [ChannelBasedDataSourceUpdateListener] updated
 * @param initialPageKey is the key which will be first provided to the [request]
 * @param request the requests to get the next page, it returns the list of values and the next key.
 * @param channelBasedDataSourceUpdateListener request state update listener, it's automatically updated while the requests are running / failing / succeeding etc.
 * @param dataSourceInvalidator a way to invalidate the latest datasource.
 * @param cache a cache which used by the datasource to gather local values
 *
 * The dataSource works the following way:
 *  - is there a cache? there is, then return that
 *  - no cache? call the [request]
 *  - depending on the response of the [request] update the [channelBasedDataSourceUpdateListener] and send the next page
 *
 *  Note: it's different from [createPagedResultFromRequest] in only the cache.
 *  Now it can return [ProvideCacheByKey.Result.CacheWithRefreshing]
 *  In this case the cache can return some values while still showing loading and gathering more, in this case when your request finished you should invalidate and return [ProvideCacheByKey.Result.Cache]
 *  The other cases are similar:
 *  [ProvideCacheByKey.Result.Cache] means no refreshing happens only the chached value is added
 *  [ProvideCacheByKey.Result.Null] means the is no cache, the [request] will be called.
 */
inline fun <Key, Value, reified Error : Throwable> createPagedResultFromRequestWithStateProvideingCache(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>,
    crossinline cache: (Key, Int) -> ProvideCacheByKey.Result<Key, Value>,
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>?,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener()
): PagedResult<Key, Value, Error> {
    val dataSourceFactory = StateProvidingListDataSourceFactory<Key, Value, Error>(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKeyAndPageSizeState { key, size -> cache(key, size) },
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size -> request(key, size) },
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResult(
        boundaryCallback = null,
        dataSourceFactory = dataSourceInvalidator?.let { DataSourceAggregatingDataSourceFactory(dataSourceFactory, it) } ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}

/**
 * Helper function which sets up your [PagedResultWithAdditionalData] with coroutine based [DataSourceUpdateListener] & [SuspendProvideDataByPagedKeyAndSize]
 *
 * @param coroutineScope in which [request] will be run and [ChannelBasedDataSourceUpdateListener] updated
 * @param initialPageKey is the key which will be first provided to the [request]
 * @param request the requests to get the next page, it returns the list of values and the next key and the additional data. Note: the additional data is only checked from the initial request.
 * @param channelBasedDataSourceUpdateListener request state update listener, it's automatically updated while the requests are running / failing / succeeding etc.
 * @param dataSourceInvalidator a way to invalidate the latest datasource.
 * @param cache a cache which used by the datasource to gather local values
 * @param additionalDataChannel the channel to provide additional data, if you need to update it later on feel free to provide your own channel
 *
 * The dataSource works the following way:
 *  - is there a cache? there is, then return that
 *  - no cache? call the [request]
 *  - depending on the response of the [request] update the [channelBasedDataSourceUpdateListener] and send the next page
 */
inline fun <Key, Value, AdditionalData, reified Error : Throwable> createPagedResultWithAdditionalDataFromRequest(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    crossinline request: suspend (Key, Int) -> Triple<List<Value>, Key, AdditionalData?>,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>? = null,
    crossinline cache: (Key, Int) -> Triple<List<Value>, Key, AdditionalData?>? = { _, _ -> null },
    additionalDataChannel : ConflatedBroadcastChannel<AdditionalData> = ConflatedBroadcastChannel()
): PagedResultWithAdditionalData<Key, Value, AdditionalData, Error> {
    val dataSourceFactory = StateProvidingListDataSourceFactory<Key, Value, Error>(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKey { key, size ->
            cache(key, size)?.mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(channel = additionalDataChannel, key = key, initialPageKey = initialPageKey)
        },
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size ->
            request(key, size).mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(channel = additionalDataChannel, key = key, initialPageKey = initialPageKey)
        },
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResultWithAdditionalData(
        boundaryCallback = null,
        additionalDataChannel = additionalDataChannel.openSubscription(),
        dataSourceFactory = dataSourceInvalidator?.let { DataSourceAggregatingDataSourceFactory(dataSourceFactory, it) } ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}

fun <Values, Key, AdditionalValue> Triple<Values, Key, AdditionalValue?>.mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(
    channel: SendChannel<AdditionalValue>,
    key: Key,
    initialPageKey: Key
): Pair<Values, Key> {
    val (values, nextKey, additionalData) = this
    if (key == initialPageKey && additionalData != null) {
        channel.offer(additionalData)
    }
    return values to nextKey
}

/**
 * Helper function which sets up your [PagedResultWithAdditionalData] with coroutine based [DataSourceUpdateListener] & [SuspendProvideDataByPagedKeyAndSize]
 *
 * @param coroutineScope in which [request] will be run and [ChannelBasedDataSourceUpdateListener] updated
 * @param initialPageKey is the key which will be first provided to the [request]
 * @param request the requests to get the next page, it returns the list of values and the next key.
 * @param channelBasedDataSourceUpdateListener request state update listener, it's automatically updated while the requests are running / failing / succeeding etc.
 * @param dataSourceInvalidator a way to invalidate the latest datasource.
 * @param cache a cache which used by the datasource to gather local values
 * @param additionalDataChannel the channel to provide additional data, if you need to update it later on feel free to provide your own channel
 *
 * The dataSource works the following way:
 *  - is there a cache? there is, then return that
 *  - no cache? call the [request]
 *  - depending on the response of the [request] update the [channelBasedDataSourceUpdateListener] and send the next page
 *
 *  Note: it's different from [createPagedResultFromRequest] in only the cache.
 *  Now it can return [ProvideCacheByKey.Result.CacheWithRefreshing]
 *  In this case the cache can return some values while still showing loading and gathering more, in this case when your request finished you should invalidate and return [ProvideCacheByKey.Result.Cache]
 *  The other cases are similar:
 *  [ProvideCacheByKey.Result.Cache] means no refreshing happens only the chached value is added
 *  [ProvideCacheByKey.Result.Null] means the is no cache, the [request] will be called.
 */
inline fun <Key, Value, AdditionalData, reified Error : Throwable> createPagedResultWithAdditionalDataFromRequestWithStateProvideingCache(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    crossinline request: suspend (Key, Int) -> Triple<List<Value>, Key, AdditionalData?>,
    crossinline cache: (Key, Int) -> ProvideCacheWithAdditionalDataByKey.Result<Key, Value, AdditionalData>,
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>?,
    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
    additionalDataChannel : ConflatedBroadcastChannel<AdditionalData> = ConflatedBroadcastChannel()
): PagedResultWithAdditionalData<Key, Value, AdditionalData, Error> {
    val dataSourceFactory = StateProvidingListDataSourceFactory<Key, Value, Error>(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKeyAndPageSizeState { key, size ->
            cache(key, size).mapProvideCacheByKeyResultAndPublishAdditionalData(channel = additionalDataChannel, key = key, initialPageKey = initialPageKey)
        },
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, size ->
            request(key, size).mapResponseFromInitialRequestWithAdditionalDataAndPublishIt(channel = additionalDataChannel, initialPageKey = initialPageKey, key = key)
        },
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResultWithAdditionalData(
        boundaryCallback = null,
        additionalDataChannel = additionalDataChannel.openSubscription(),
        dataSourceFactory = dataSourceInvalidator?.let { DataSourceAggregatingDataSourceFactory(dataSourceFactory, it) } ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}

fun <PageKey, Data, AdditionalData> ProvideCacheWithAdditionalDataByKey.Result<PageKey, Data, AdditionalData>.mapProvideCacheByKeyResultAndPublishAdditionalData(
    channel: SendChannel<AdditionalData>,
    key: PageKey,
    initialPageKey: PageKey
): ProvideCacheByKey.Result<PageKey, Data> =
    when (this) {
        is ProvideCacheWithAdditionalDataByKey.Result.Cache -> {
            if (key == initialPageKey && additionalData != null) {
                @Suppress("UNCHECKED_CAST")
                channel.offer(additionalData as AdditionalData)
            }

            ProvideCacheByKey.Result.Cache(data, pageKey)
        }
        is ProvideCacheWithAdditionalDataByKey.Result.Null -> ProvideCacheByKey.Result.Null()
        is ProvideCacheWithAdditionalDataByKey.Result.CacheWithRefreshing -> {
            if (key == initialPageKey && additionalData != null) {
                @Suppress("UNCHECKED_CAST")
                channel.offer(additionalData as AdditionalData)
            }

            ProvideCacheByKey.Result.CacheWithRefreshing(data, pageKey)
        }
    }