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