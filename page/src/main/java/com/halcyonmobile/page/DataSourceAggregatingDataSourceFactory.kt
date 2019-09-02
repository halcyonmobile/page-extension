/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.page

import androidx.paging.DataSource

/**
 * A [DataSource.Factory] wrapper which aggregates the created [DataSource]s into the [aggregator] when they are created.
 */
class DataSourceAggregatingDataSourceFactory<Key, Value>(
    private val dataSourceFactory: DataSource.Factory<Key, Value>,
    private val aggregator: DataSourceAggregator<Key, Value>
) : DataSource.Factory<Key, Value>() {

    override fun create(): DataSource<Key, Value> =
        dataSourceFactory.create().also(aggregator::addDataSource)

    interface DataSourceAggregator<Key, Value> {

        fun addDataSource(dataSource: DataSource<Key, Value>)
    }
}