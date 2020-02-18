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