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