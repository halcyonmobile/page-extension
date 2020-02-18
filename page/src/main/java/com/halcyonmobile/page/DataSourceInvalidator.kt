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
import java.lang.ref.WeakReference

/**
 * A [DataSource] aggregator which holds weak references to the data sources and able to invalidate them.
 */
class DataSourceInvalidator<Key, Value> : DataSourceAggregatingDataSourceFactory.DataSourceAggregator<Key, Value> {

    private var dataSources = mutableListOf<WeakReference<DataSource<*, *>>>()

    override fun addDataSource(dataSource: DataSource<Key, Value>) {
        synchronized(dataSources) {
            dataSources.add(WeakReference(dataSource))
        }
    }

    operator fun invoke() {
        synchronized(dataSources) {
            dataSources.mapNotNull(WeakReference<DataSource<*, *>>::get).forEach(DataSource<*, *>::invalidate)
            dataSources.clear()
        }
    }
}