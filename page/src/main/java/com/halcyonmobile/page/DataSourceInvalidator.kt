/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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