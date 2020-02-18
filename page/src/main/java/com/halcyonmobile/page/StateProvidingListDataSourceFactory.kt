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