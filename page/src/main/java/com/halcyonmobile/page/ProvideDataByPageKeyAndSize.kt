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
 * Interface to provide [Data] data by pagination to a [StateProvidingListDataSourceFactory] and [StateProvidingListDataSource]
 */
interface ProvideDataByPageKeyAndSize<Data, PageKey, Error> {

    operator fun invoke(pageKey: PageKey, pageSize: Int, callback: (Result<Data, PageKey, Error>) -> Unit)

    sealed class Result<Data, PageKey, Error> {
        data class Success<Data, PageKey, Error>(val data: List<Data>, val nextKey: PageKey) : Result<Data, PageKey, Error>()
        data class Error<Data, PageKey, Error>(val error: Error) : Result<Data, PageKey, Error>()
    }
}

@Suppress("FunctionName")
internal inline fun <Data, PageKey, Error> ProvideDataByPageKeyAndSize(crossinline provideMethodKey: (pageKey: PageKey, pageSize: Int, callback: (ProvideDataByPageKeyAndSize.Result<Data, PageKey, Error>) -> Unit) -> Unit) =
    object : ProvideDataByPageKeyAndSize<Data, PageKey, Error> {
        override fun invoke(pageKey: PageKey, pageSize: Int, callback: (ProvideDataByPageKeyAndSize.Result<Data, PageKey, Error>) -> Unit) =
            provideMethodKey(pageKey, pageSize, callback)
    }