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

import androidx.paging.PageKeyedDataSource

/**
 * DataSource which provides it's state to a [dataSourceUpdateListener]
 *
 * It's a PageKeyedDataSource, every [Result] provided from [provideDataByPageKeyAndSize] should contain the nextPageKey.
 * The [initialPageKey] is used to load the initial data.
 */
class StateProvidingListDataSource<Data, PageKey, Error>(
    private val initialPageKey: PageKey,
    private val provideDataByPageKeyAndSize: ProvideDataByPageKeyAndSize<Data, PageKey, Error>,
    private val provideCacheByKey: ProvideCacheByKey<PageKey, Data>,
    private val dataSourceUpdateListener: DataSourceUpdateListener<Error>
) : PageKeyedDataSource<PageKey, Data>() {

    override fun loadInitial(params: LoadInitialParams<PageKey>, callback: LoadInitialCallback<PageKey, Data>) {
        when (val cache = provideCacheByKey(initialPageKey, params.requestedLoadSize)) {
            is ProvideCacheByKey.Result.Cache -> {
                callback.onResult(cache.data, null, cache.pageKey)
                dataSourceUpdateListener(if (cache.data.isEmpty()) DataSourceState.Empty() else DataSourceState.Normal())
            }
            is ProvideCacheByKey.Result.Null -> {
                dataSourceUpdateListener(DataSourceState.InitialLoading())
                provideDataByPageKeyAndSize(initialPageKey, params.requestedLoadSize) { result ->
                    when (result) {
                        is ProvideDataByPageKeyAndSize.Result.Success<Data, PageKey, Error> -> {
                            callback.onResult(result.data, null, result.nextKey)
                            val dataSourceState: DataSourceState<Error> = if (result.data.isEmpty()) DataSourceState.Empty() else DataSourceState.Normal()
                            dataSourceUpdateListener(dataSourceState)
                        }
                        is ProvideDataByPageKeyAndSize.Result.Error<Data, PageKey, Error> -> {
                            dataSourceUpdateListener(DataSourceState.ErrorLoadingInitial(result.error) {
                                loadInitial(params, callback)
                            })
                        }
                    }
                }
            }
            is ProvideCacheByKey.Result.CacheWithRefreshing -> {
                callback.onResult(cache.data, null, cache.pageKey)
                dataSourceUpdateListener(DataSourceState.InitialLoading())
            }
        }
    }

    override fun loadAfter(params: LoadParams<PageKey>, callback: LoadCallback<PageKey, Data>) {
        if (params.key == initialPageKey) {
            callback.onResult(emptyList(), initialPageKey)
            return
        }
        dataSourceUpdateListener(DataSourceState.LoadingMore())
        provideDataByPageKeyAndSize(params.key, params.requestedLoadSize) { result ->
            when (result) {
                is ProvideDataByPageKeyAndSize.Result.Success<Data, PageKey, Error> -> {
                    val resultDataList = result.data
                    callback.onResult(resultDataList, result.nextKey)
                    dataSourceUpdateListener(if (resultDataList.isEmpty()) DataSourceState.EndReached() else DataSourceState.Normal())
                }
                is ProvideDataByPageKeyAndSize.Result.Error<Data, PageKey, Error> -> {
                    dataSourceUpdateListener(DataSourceState.ErrorLoadingMore(result.error) {
                        loadAfter(params, callback)
                    })
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<PageKey>, callback: LoadCallback<PageKey, Data>) {
        // isn't necessary, since we only load more
    }
}