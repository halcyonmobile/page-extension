/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page

/**
 * Interface to provide initial cached [Data] and [AdditionalData] synchronously to the [StateProvidingListDataSource]
 */
interface ProvideCacheWithAdditionalDataByKey<PageKey, Data, AdditionalData> {
    operator fun invoke(pageKey: PageKey, pageSize: Int): Result<PageKey, Data, AdditionalData>

    sealed class Result<PageKey, Data, AdditionalData> {
        class Cache<PageKey, Data, AdditionalData>(val data: List<Data>, val pageKey: PageKey, val additionalData: AdditionalData?) : Result<PageKey, Data, AdditionalData>()
        class Null<PageKey, Data, AdditionalData> : Result<PageKey, Data, AdditionalData>()
        class CacheWithRefreshing<PageKey, Data, AdditionalData>(
            val data: List<Data>,
            val pageKey: PageKey,
            val additionalData: AdditionalData?
        ) : Result<PageKey, Data, AdditionalData>()
    }
}

inline fun <PageKey, Data, AdditionalData> ProvideCacheWithAdditionalDataByKey(
    crossinline provideCacheByKey: (pageKey: PageKey, pageSize: Int) -> Triple<List<Data>, PageKey, AdditionalData?>?
): ProvideCacheWithAdditionalDataByKey<PageKey, Data, AdditionalData> =
    object : ProvideCacheWithAdditionalDataByKey<PageKey, Data, AdditionalData> {
        override fun invoke(pageKey: PageKey, pageSize: Int): ProvideCacheWithAdditionalDataByKey.Result<PageKey, Data, AdditionalData> =
            provideCacheByKey(pageKey, pageSize)?.let { (data, key, additionalData) ->
                ProvideCacheWithAdditionalDataByKey.Result.Cache(data, key, additionalData)
            } ?: ProvideCacheWithAdditionalDataByKey.Result.Null()
    }

inline fun <PageKey, Data, AdditionalData> ProvideCacheWithAdditionalAndCacheStateDataByKey(
    crossinline provideCacheByKeyState: (pageKey: PageKey, pageSize: Int) -> ProvideCacheWithAdditionalDataByKey.Result<PageKey, Data, AdditionalData>
): ProvideCacheWithAdditionalDataByKey<PageKey, Data, AdditionalData> =
    object : ProvideCacheWithAdditionalDataByKey<PageKey, Data, AdditionalData> {
        override fun invoke(pageKey: PageKey, pageSize: Int): ProvideCacheWithAdditionalDataByKey.Result<PageKey, Data, AdditionalData> =
            provideCacheByKeyState(pageKey, pageSize)
    }