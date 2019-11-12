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

import androidx.paging.DataSource

/**
 * DataSourceFactory which provides states of the lastly created DataSource into [dataUpdateListenerFactory].
 *
 * The [provideDataByPageKeyAndSize] is used by the created [DataSource]s to get the data.
 * The [initialPageKey] is used by the created [DataSource]s to get the initial data. It will be passed to [provideDataByPageKeyAndSize]
 */
class StateProvidingListDataSourceFactory<PageKey, Data, Error> constructor(
    private val initialPageKey: PageKey,
    private val provideDataByPageKeyAndSize: ProvideDataByPageKeyAndSize<Data, PageKey, Error>,
    private val provideCacheByKey: ProvideCacheByKey<PageKey, Data>,
    private val dataSourceUpdateListener: DataSourceUpdateListener<Error>
) : DataSource.Factory<PageKey, Data>() {

    private var currentCancelableDataUpdateListener: CancelableDataUpdateListener<Error>? = null

    override fun create(): DataSource<PageKey, Data> {
        currentCancelableDataUpdateListener?.cancel()

        val cancelableDataUpdateListener = CancelableDataUpdateListener(dataSourceUpdateListener)
        currentCancelableDataUpdateListener = cancelableDataUpdateListener
        return StateProvidingListDataSource(initialPageKey, provideDataByPageKeyAndSize, provideCacheByKey, cancelableDataUpdateListener)
    }

    private class CancelableDataUpdateListener<Error>(private val dataSourceUpdateListener: DataSourceUpdateListener<Error>) :
        DataSourceUpdateListener<Error> {
        private var isCanceled = false

        override fun invoke(state: DataSourceState<Error>) {
            if (isCanceled) return

            dataSourceUpdateListener(state)
        }

        fun cancel() {
            isCanceled = true
        }
    }
}