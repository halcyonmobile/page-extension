/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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