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
package com.halcyonmobile.page

/**
 * Interface to provide initial cached [Data] synchronously to the [StateProvidingListDataSource]
 */
interface ProvideCacheByKey<PageKey, Data> {
    operator fun invoke(pageKey: PageKey, pageSize: Int): Result<PageKey, Data>

    sealed class Result<PageKey, Data> {
        class Cache<PageKey, Data>(val data: List<Data>, val pageKey: PageKey) : Result<PageKey, Data>()
        class Null<PageKey, Data> : Result<PageKey, Data>()
        class CacheWithRefreshing<PageKey, Data>(val data: List<Data>, val pageKey: PageKey) : Result<PageKey, Data>()
    }
}

inline fun <PageKey, Data> ProvideCacheByKey(crossinline provideCacheByKey: (pageKey: PageKey, pageSize: Int) -> Pair<List<Data>, PageKey>?): ProvideCacheByKey<PageKey, Data> =
    object : ProvideCacheByKey<PageKey, Data> {
        override fun invoke(pageKey: PageKey, pageSize: Int): ProvideCacheByKey.Result<PageKey, Data> =
            provideCacheByKey(pageKey, pageSize)?.let { (data, key) ->
                ProvideCacheByKey.Result.Cache(data, key)
            } ?: ProvideCacheByKey.Result.Null<PageKey, Data>()
    }

inline fun <PageKey, Data> ProvideCacheByKeyAndPageSizeState(crossinline provideCacheByKeyState: (pageKey: PageKey, pageSize: Int) -> ProvideCacheByKey.Result<PageKey, Data>): ProvideCacheByKey<PageKey, Data> =
    object : ProvideCacheByKey<PageKey, Data> {
        override fun invoke(pageKey: PageKey, pageSize: Int): ProvideCacheByKey.Result<PageKey, Data> =
            provideCacheByKeyState(pageKey, pageSize)
    }