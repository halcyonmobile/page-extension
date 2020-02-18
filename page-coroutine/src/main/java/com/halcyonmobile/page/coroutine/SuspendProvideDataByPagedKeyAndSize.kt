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

import com.halcyonmobile.page.ProvideDataByPageKeyAndSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * [ProvideDataByPageKeyAndSize] implementation which uses suspend functions instead of callbacks.
 */
abstract class SuspendProvideDataByPagedKeyAndSize<Data, PageKey, Error>(private val coroutineScope: CoroutineScope) :
    ProvideDataByPageKeyAndSize<Data, PageKey, Error> {

    override fun invoke(pageKey: PageKey, pageSize: Int, callback: (ProvideDataByPageKeyAndSize.Result<Data, PageKey, Error>) -> Unit) {
        coroutineScope.launch {
            callback(invoke(pageKey, pageSize))
        }
    }

    abstract suspend operator fun invoke(pagedKey: PageKey, pageSize: Int): ProvideDataByPageKeyAndSize.Result<Data, PageKey, Error>
}

@Suppress("FunctionName")
inline fun <PageKey, Data, reified Error> SuspendProvideDataByPagedKeyAndSize(
    coroutineScope: CoroutineScope,
    crossinline provideFunction: suspend (pagedKey: PageKey, pageSize: Int) -> Pair<List<Data>, PageKey>
) =
    object : SuspendProvideDataByPagedKeyAndSize<Data, PageKey, Error>(coroutineScope) {
        override suspend fun invoke(pagedKey: PageKey, pageSize: Int): ProvideDataByPageKeyAndSize.Result<Data, PageKey, Error> =
            try {
                val (data, nextPageKey) = provideFunction(pagedKey, pageSize)
                ProvideDataByPageKeyAndSize.Result.Success(data, nextPageKey)
            } catch (throwable: Throwable) {
                if (throwable is Error) {
                    ProvideDataByPageKeyAndSize.Result.Error(throwable as Error)
                } else {
                    throw throwable
                }
            }
    }