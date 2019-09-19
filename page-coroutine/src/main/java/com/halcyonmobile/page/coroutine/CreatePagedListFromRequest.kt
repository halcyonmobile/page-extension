/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.DataSourceAggregatingDataSourceFactory
import com.halcyonmobile.page.DataSourceInvalidator
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideCacheByKey
import com.halcyonmobile.page.ProvideCacheByKeyAndPageSizeState
import com.halcyonmobile.page.StateProvidingListDataSourceFactory
import kotlinx.coroutines.CoroutineScope

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